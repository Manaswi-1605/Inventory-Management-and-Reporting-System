export const FormatUtils = {
  currency: (val) =>
    new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(val),
 
  number: (val) =>
    new Intl.NumberFormat('en-IN').format(val),
 
  capitalize: (str) =>
    str ? str.charAt(0).toUpperCase() + str.slice(1).toLowerCase() : '',
 
  truncate: (str, max = 30) =>
    str && str.length > max ? str.slice(0, max) + '…' : str,
};