export const formatCurrency = (val: number | string | undefined | null, decimals = 4): string => {
  if (val === undefined || val === null) return '$0.00';
  const num = typeof val === 'string' ? parseFloat(val) : val;
  if (isNaN(num)) return '$0.00';
  if (num === 0) return '$0.00';
  if (num >= 100) return `$${num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  if (num >= 1) return `$${num.toFixed(2)}`;
  if (num >= 0.01) return `$${num.toFixed(3)}`;
  return `$${num.toFixed(decimals)}`;
};

export const formatNumber = (val: number | string | undefined | null): string => {
  if (val === undefined || val === null) return '0';
  const num = typeof val === 'string' ? parseInt(val, 10) : val;
  if (isNaN(num)) return '0';
  return num.toLocaleString('en-US');
};

export const formatTokens = (val: number | string | undefined | null): string => {
  if (val === undefined || val === null) return '0';
  const num = typeof val === 'string' ? parseInt(val, 10) : val;
  if (isNaN(num)) return '0';
  if (num >= 1_000_000) return `${(num / 1_000_000).toFixed(2)}M`;
  if (num >= 1_000) return `${(num / 1_000).toFixed(1)}k`;
  return num.toString();
};

export const formatLatency = (val: number | string | undefined | null): string => {
  if (val === undefined || val === null) return '0 ms';
  const num = typeof val === 'string' ? parseFloat(val) : val;
  if (isNaN(num)) return '0 ms';
  return `${Math.round(num)} ms`;
};

export const formatPercentage = (val: number | string | undefined | null): string => {
  if (val === undefined || val === null) return '0%';
  let num = typeof val === 'string' ? parseFloat(val) : val;
  if (isNaN(num)) return '0%';
  if (num <= 1.0) num = num * 100;
  return `${num.toFixed(1)}%`;
};

export const formatRelativeTime = (isoString?: string | null): string => {
  if (!isoString) return 'just now';
  try {
    const diff = (Date.now() - new Date(isoString).getTime()) / 1000;
    if (diff < 60) return `${Math.max(1, Math.floor(diff))}s ago`;
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return `${Math.floor(diff / 86400)}d ago`;
  } catch (e) {
    return 'recently';
  }
};
