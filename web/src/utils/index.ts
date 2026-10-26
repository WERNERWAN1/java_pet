// import { resolve } from 'path';
// const fs = require('fs');
//
// function pathResolve(dir: string) {
//   return resolve(process.cwd(), '.', dir);
// }
//
// export const getFolder = (path: any) => {
//   const components: Array<string> = [];
//   const files = fs.readdirSync(path);
//   files.forEach(function (item: string) {
//     const stat = fs.lstatSync(path + '/' + item);
//     if (stat.isDirectory() === true && item != 'components') {
//       components.push(path + '/' + item);
//       components.push(pathResolve(path + '/' + item));
//     }
//   });
//   return components;
// };

export function getFormatTime(dateTime, flag) {
  if (dateTime === null || dateTime === undefined || dateTime === '') {
    return '';
  }

  let date: Date;
  if (typeof dateTime === 'number') {
    // 数字时间戳：10 位为秒级，13 位为毫秒级
    date = new Date(dateTime < 1e12 ? dateTime * 1000 : dateTime);
  } else if (typeof dateTime === 'string') {
    const str = dateTime.trim();
    if (/^\d+$/.test(str)) {
      // 纯数字字符串：按秒/毫秒时间戳处理
      const num = parseInt(str, 10);
      date = new Date(str.length <= 10 ? num * 1000 : num);
    } else {
      // 兼容 'yyyy-MM-dd HH:mm:ss'、'yyyy-MM-dd'T'HH:mm:ss'、ISO 等格式
      // 先直接解析（ISO 带时区的字符串必须原样解析），失败再尝试替换分隔符兼容 Safari
      let parsed = new Date(str);
      if (isNaN(parsed.getTime())) {
        parsed = new Date(str.replace(/-/g, '/'));
      }
      date = parsed;
    }
  } else {
    date = new Date(dateTime);
  }

  if (isNaN(date.getTime())) {
    return '';
  }

  const pad = (n: number) => (n < 10 ? '0' + n : '' + n);
  const YY = date.getFullYear();
  const MM = pad(date.getMonth() + 1);
  const DD = pad(date.getDate());

  if (flag) { // flag 为 true，显示时分秒格式
    const hh = pad(date.getHours());
    const mm = pad(date.getMinutes());
    const ss = pad(date.getSeconds());
    // 返回时间格式： 2020-11-09 13:14:52
    return YY + '-' + MM + '-' + DD + ' ' + hh + ':' + mm + ':' + ss;
  } else {
    // 返回时间格式： 2020-11-09
    return YY + '-' + MM + '-' + DD;
  }
}
