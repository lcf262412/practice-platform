import FileSaver from 'file-saver';
// 不使用import * 的方式导入xlsx会报错
import * as XLSX from 'xlsx';

/**
 * @demoJx(2021/4/19) json转excel文件
 * @param {Object} jsonData 传入的json数据
 * @param {String} options.fileName 导出的文件名称
 * @param {Function} options.parseDataFun([传入的json数据]) json数据前处理方法,返回{headerList,headers,rows}数据
 * @param {Function} options.callback 回调方法
 */

// Json数组转换为Xlsx，提取自上一个方法的核心逻辑
export function json2Xlsx(data, tableName) {
  const sheet = [...data];

  // 创建一个sheet表格   使用json_to_sheet, 数据格式比较为  数组包对象, 不然会报错
  const worksheet = XLSX.utils.json_to_sheet(sheet, {

    /* 
     *这里可以通过设置header, 来对导出数据 列 顺序的排序
     *实测可以只写一部分, 也可以整体排序
     *["id", "name", "age"] 相当于把上面整个表头给限制顺序了
     *header: ["age"],
     *跳过 Header, 就是把原来表格数据的表头去掉了, headerReplace渲染的数据 "冒充" 表头了
     *skipHeader: true,
     */
  });

  // 简单理解为在内存中 创建一个xlsx 文件
  const newWorkBook = XLSX.utils.book_new();
  // 把worksheet添加到workbook中
  XLSX.utils.book_append_sheet(newWorkBook, worksheet, 'SheetJS');
  // 写入文件, CHROME浏览器会直接下载, 后面的是文件名称和后缀
  XLSX.writeFile(newWorkBook, tableName + '.xlsx');
}

/*
 * 一个确定有效的导出表格为Excel的方法
 * 导出对象为页面上的表格元素
 * @param {String} index 标签编号
 * @returns 
 */
export function table2Excel(index) {
  var wb = XLSX.utils.table_to_book(
    // querySelector对应选择器的元素标签
    document.querySelector('.result-table'- + index)
  );
  var wbout = XLSX.write(wb, {
    bookType: 'xlsx',
    bookSST: true,
    type: 'array',
  });
  try {
    let timeStamp = new Date().valueOf();
    let fileName = '结果_' + timeStamp + '.xlsx';
    FileSaver.saveAs(
      new Blob([wbout], { type: 'application/octet-stream' }),
      fileName
    );
  } catch (e) {
  }
  return wbout;
}


// 这个对象包含了Json转为CSV的流程
export var JSonToCSV = {
  
  /*
   * obj是一个对象，其中包含有：
   * ## data 是导出的具体数据
   * ## fileName 是导出时保存的文件名称 是string格式
   * ## showLabel 表示是否显示表头 默认显示 是布尔格式
   * ## columns 是表头对象，且title和key必须一一对应，包含有
   *    title:[],  表头展示的文字
   *    key:[],  获取数据的Key
   *    formatter: function()  自定义设置当前数据的 传入(key, value)
   */
  setDataConver: function(obj) {
    var data = obj['data'],
      ShowLabel = typeof obj['showLabel'] === 'undefined' ? true : obj['showLabel'],
      fileName = (obj['fileName'] || 'UserExport') + '.csv',
      columns = obj['columns'] || {
        title: [],
        key: [],
        formatter: undefined
      };
    ShowLabel = typeof ShowLabel === 'undefined' ? true : ShowLabel;
    var row = '', CSV = '', key;
    if (ShowLabel) {
      if (columns.title.length) {
        columns.title.map(function(n) {
          row += n + ',';
        });
      } 
      else {
        for (key in data[0]) row += key + ',';
      }
      CSV= row.slice(0, -1)+'\r\n'; // 删除最后一个,号，即a,b, => a,b
    }
    data.map(function(n) {
      row = '';
      if (columns.key.length) {
        columns.key.map(function(m) {
          if(typeof columns.formatter === 'function'){
            row += '"' + (columns.formatter(m, n[m]) || n[m]) + '",';
          }
          else{
            row += '"' + (n[m]) + '",';
          }
        });
      } 
      else {
        for (key in n) {
          if(typeof columns.formatter === 'function'){
            row += '"' + (columns.formatter(key, n[key]) || n[key]) + '",';
          }
          else{
            row += '"' + (n[key]) + '",';
          }
        }
      }
      row.slice(0, row.length - 1); // 删除最后一个,
      CSV += row + '\r\n'; // 添加换行符号
    });
    if(!CSV) return;
    this.saveAs(fileName, CSV);
  },
  saveAs: function(fileName, csvData) {
    var bw = this.browser();
    if(!bw['edge'] ||  !bw['ie']) {
      var alink = document.createElement('a');
      alink.id = 'linkDwnldLink';
      alink.href = this.getDownloadUrl(csvData);
      document.body.appendChild(alink);
      var linkDom = document.getElementById('linkDwnldLink');
      linkDom.setAttribute('download', fileName);
      linkDom.click();
      document.body.removeChild(linkDom);
    }
  },
  getDownloadUrl: function(csvData) {
    var _utf = '\uFEFF'; // 为了使Excel以utf-8的编码模式，同时也是解决中文乱码的问题
    if (window.Blob && window.URL && window.URL.createObjectURL) {
      csvData = new Blob([_utf + csvData], {
        type: 'text/csv'
      });
      return URL.createObjectURL(csvData);
    }
  },
  browser: function() {
    var Sys = {};
    var ua = navigator.userAgent.toLowerCase();
    var s;
    // eslint-disable-next-line no-cond-assign
    (s = ua.indexOf('edge') !== - 1 ? Sys.edge = 'edge' : ua.match(/rv:([\d.]+)\) like gecko/)) ? Sys.ie = s[1]:
    // eslint-disable-next-line no-cond-assign
      (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
      // eslint-disable-next-line no-cond-assign
        (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
        // eslint-disable-next-line no-cond-assign
          (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
          // eslint-disable-next-line no-cond-assign
            (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
            // eslint-disable-next-line no-cond-assign
              (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
    return Sys;
  }
};
