// 날짜 포맷: "2025-05-15" → "5.15"
export const formatDate = (date: string) => {
  const [ , month, day] = date.split('-');
  return `${parseInt(month)}.${parseInt(day)}`;
};
export const formatTime = (isoString: string) => {
  const date = new Date(isoString);

  let hours = date.getHours(); // 0-23시
  const minutes = date.getMinutes(); // 분은 현재 예시에서 사용하지 않지만, 필요할 경우를 대비

  let ampm = '';
  if (hours >= 12) {
    ampm = '오후';
    if (hours > 12) {
      hours -= 12; // 13시 -> 1시, 14시 -> 2시 등으로 변환
    }
  } else {
    ampm = '오전';
    if (hours === 0) {
      hours = 12; // 0시 -> 오전 12시
    }
  }

  // 원하는 형식으로 반환 (분도 필요하다면 `:${minutes.toString().padStart(2, '0')}` 추가)
  return `${ampm} ${hours}시`;
}

// 18:00 -> 오후 6시 
export function formatToKoreanTime(time: string): string {
  const [hourStr, minuteStr] = time.split(':');
  const hour = Number(hourStr);
  const period = hour < 12 ? '오전' : '오후';
  const hour12 = hour % 12 === 0 ? 12 : hour % 12;
  return `${period} ${hour12}:${minuteStr}`;
}