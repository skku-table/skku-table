import {onSchedule} from "firebase-functions/v2/scheduler";
import * as admin from "firebase-admin";

admin.initializeApp();
const db = admin.firestore();

export const sendReservationNotifications = onSchedule(
  {
    schedule: "every 1 minutes",
    timeZone: "Asia/Seoul",
  },
  async () => {
    const now = admin.firestore.Timestamp.now();
    const tenMinutesLater = admin.firestore.Timestamp.fromDate(
      new Date(now.toDate().getTime() + 10 * 60 * 1000)
    );

    const snapshot = await db
      .collection("reservations")
      .where("notified", "==", false)
      .where("reservationTime", ">=", now)
      .where("reservationTime", "<=", tenMinutesLater)
      .get();

    if (snapshot.empty) {
      console.log("No upcoming reservations.");
      return;
    }

    await Promise.all(
      snapshot.docs.map(async (doc) => {
        const {pushToken, boothName, reservationTime} = doc.data() as {
          pushToken?: string;
          boothName?: string;
          reservationTime?: FirebaseFirestore.Timestamp;
        };
        if (!pushToken || !reservationTime) return;

        // 1) Firestore Timestamp → JS Date
        const date = reservationTime.toDate();

        // 2) 한국 시각으로 포맷
        const kst = date.toLocaleTimeString("ko-KR", {
          timeZone: "Asia/Seoul",
          hour: "2-digit",
          minute: "2-digit",
        }); // e.g. "22:33"

        try {
          await admin.messaging().send({
            token: pushToken,
            notification: {
              title: "부스 예약 10분 전 알림",
              body: `${kst}에 ${boothName} 부스 예약이 있어요!`,
            },
          });
          await doc.ref.update({notified: true});
          console.log(`Sent notification for ${doc.id} at ${kst}`);
        } catch (err) {
          console.error("FCM 전송 실패:", err);
        }
      })
    );
  }
);
