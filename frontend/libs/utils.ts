// 날짜 포맷: "2025-05-15" → "5.15"
export const formatDate = (date: string) => {
  const [ , month, day] = date.split('-');
  return `${parseInt(month)}.${parseInt(day)}`;
};

// 18:00 -> 오후 6시 
export function formatToKoreanTime(time: string): string {
  const [hourStr, minuteStr] = time.split(':');
  const hour = Number(hourStr);
  const period = hour < 12 ? '오전' : '오후';
  const hour12 = hour % 12 === 0 ? 12 : hour % 12;
  return `${period} ${hour12}:${minuteStr}`;
}