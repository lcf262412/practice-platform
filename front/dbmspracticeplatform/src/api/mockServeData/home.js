import Mock from 'mockjs';

export default {
  getData: () => {
    return {
      code: '0000',
      data: {
        name: '002',
      },
    };
  },

  // 模式内元数据mock
  mockSchemaMetadata: () => {
    return {
      code: '0000',
      message: 'success',
      result: {
        schemaMetaDataMap: {
          viewList: [],
          tableList: [{ 
            constraintList: [],indexList: [],oid: 33533,tablename: 'j',  
            columnList: [
              {columnidx: 1,columndisplay: 'character(5)',columnname: 'jno',},
              {columnidx: 2,columndisplay: 'character(20)',columnname: 'jname',},
              {columnidx: 3,columndisplay: 'character(20)',columnname: 'city',},
            ],},
          {constraintList: [],indexList: [],oid: 33536,tablename: 'p',
            columnList: [
              {columnidx: 1,columndisplay: 'character(5)',columnname: 'pno',},
              {columnidx: 2,columndisplay: 'character(20)',columnname: 'pname',},
              {columnidx: 3,columndisplay: 'character(10)',columnname: 'color',},
              {columnidx: 4,columndisplay: 'integer',columnname: 'weight',},
            ],},
          {constraintList: [],indexList: [],oid: 33539,tablename: 's',
            columnList: [
              {columnidx: 1,columndisplay: 'character(5)',columnname: 'sno',},
              {columnidx: 2,columndisplay: 'character(20)',columnname: 'sname',},
              {columnidx: 3,columndisplay: 'integer',columnname: 'status',},
              {columnidx: 4,columndisplay: 'character(20)',columnname: 'city',},
            ],},
          {constraintList: [], indexList: [], oid: 33542,tablename: 'spj',
            columnList: [
              {columnidx: 1,columndisplay: 'character(5)',columnname: 'sno',},
              {columnidx: 2,columndisplay: 'character(5)',columnname: 'pno',},
              {columnidx: 3,columndisplay: 'character(5)',columnname: 'jno',},
              {columnidx: 4,columndisplay: 'integer',columnname: 'qty',},
            ],},
          {constraintList: [],indexList: [],oid: 34685,tablename: 'check_error',
            columnList: [
              {columnidx: 1,columndisplay: 'timestamp without time zone',columnname: 'starttime',},
              {columnidx: 2,columndisplay: 'timestamp without time zone',columnname: 'endtime',},
              {columnidx: 3,columndisplay: 'text',columnname: 'exceptions',},
            ],},
          ],
          funcList: [
            { funcname: 'check_other',oid: 16931,},{ funcname: 'check_select',oid: 16929,},
            { funcname: 'replacefirst',oid: 16930,},
          ],
        },
        schemaOid: 2200,
      },};},};
