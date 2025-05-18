// 날짜 포맷: "2025-05-15" → "5.15"
export const formatDate = (date: string) => {
    const [ , month, day] = date.split('-');
    return `${parseInt(month)}.${parseInt(day)}`;
  };