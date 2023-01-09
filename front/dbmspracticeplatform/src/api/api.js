import axios from './axios';
import qs from 'qs';

// 示例接口
export const getMenu = (param) => {
  return axios.request({
    url: '/permission/getMenu',
    method: 'post',
    data: param,
  });
};
// 示例接口
export const getData = () => {
  return axios.request({
    // mock接口的完整请求地址就是这个相对url
    url: '/home/getData',
  });
};

// 登录
export const login = (params) => {
  return axios.request({
    url: '/user/login',
    method: 'post',
    data: params,
    contentType: 'application/x-www-form-urlencoded',
  });
};
// 关闭数据库连接
export const disConnect = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/practice/disconnect',
    method: 'post',
    data:req
  })
}
// 导入数据表
export const uploadTable = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/practice/uploadTable',
    method: 'post',
    data:params,
    contentType: 'multipart/form-data'
  })
}
// 导入班级学生
export const importStudents = (params) => {
  
  return axios.request({
    url:'/student/importStudents',
    method: 'post',
    data:params,
    contentType: 'multipart/form-data'
  })
}
// 查询学生名单模板下载
export const downloadTemplate = () => {
 
  return axios.request({
    url: '/student/downloadTemplate' ,
    method: 'get',
    data: {},
    responseType: 'blob',
  });
};
// 执行sql语句
export async function executeSql(params) {
  return axios.request({
    url: '/api/actuator/code',
    method: 'post',
    data: params,
  });
}
// 修改用户密码
export const modifyUserPwd = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/user/changePwd',
    method: 'post',
    data:req
  })
}
// 查询指定试题内容（学生）
export const getQuestionDetailsById = (param) => {
  return axios.request({
    url: '/question/getQuestionStudent?id=' + param,
    method: 'get',
    data: {},
  });
};

// 查询指定试题内容（学生）
export const getQuestionDetailsByOrder = (params) => {
  let temp = qs.stringify(params)
  return axios.request({
    url: '/questinexer/getQuestionStudentByExerciseIdAndOrderId?' + temp,
    method: 'get',
  });
};



// 能力评估提交SQL答案，以及作答思路
export const submitQuestAnswer = (param) => {
  var req = qs.stringify(param);

  return axios.request({
    url: '/judge/judge',
    
    method: 'post',
    data: req,
    contentType: 'application/x-www-form-urlencoded',
  });
};

// 学生查看当前试卷的题目列表
export async function getPapaerQuestion(params) {
  var query = qs.stringify(params);
  return axios.request({
    url: '/questinexer/getExerAllContent?' + query,
    method: 'get',
  });
}

// 查找指定题目作答记录
export const getAnswerRecord = (params) => {
  var temp = qs.stringify(params);
  return axios.request({
    url: '/stuanswer/getStuAnswer?' + temp,
    method: 'get',
    data: {},
  });
};

// 学生用户连接数据库
export const connectDBStudent = (params) => {
  var req = qs.stringify(params);

  return axios.request({
    url: '/practice/connectForStudent',
    method: 'post',
    data: req,
  });
};

// 教师连接实践数据库
export const connectDBTeacher = (params) => {
  var req = qs.stringify(params);

  return axios.request({
    url: '/practice/connectForTeacher',
    method: 'post',
    data: req,
  });
};
// 连接答题数据库
export const connectJudgeDb = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/judge/connectJudgeDatabase',
    method: 'post',
    data:req
  })
}
// 关闭答题数据库
export const disconnectJudgeDb = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/judge/disconnectJudgeConnect',
    method: 'post',
    data:req
  })
}
// 查询题目全部测试用例
export const selectTestCases = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/judge/selectTestCase',
    method: 'post',
    data:req
  })
}
// 新增测试用例
export const addOneTestCase = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/judge/addOneTestCase',
    method: 'post',
    data:req
  })
}
// 修改测试用例
export const updateOneTestCase = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/judge/updateOneTestCase',
    method: 'post',
    data:req
  })
}
// 删除测试用例
export const deleteOneTestCase = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/judge/deleteOneTestCase',
    method: 'post',
    data:req
  })
}
// 删除全部测试用例
export const deleteAllTestCase = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/judge/deleteAllTestCase',
    method: 'post',
    data:req
  })
}
// 获取用户模式列表
export const getUserSchema = (params) => {
  var req = qs.stringify(params);
  return axios.request({
    url: '/practice/getUserSchema',
    method: 'post',
    data: req,
  });
};

// 获取模式内元数据
export async function getSchemaMetadata(params) {
  var req = qs.stringify(params);
  return axios.request({
    
    url: '/practice/getSchemaMetadata',
    method: 'post',
    data: req,
  });
}

// 实践sql语句执行
export const execSql = (params) => {
  var req = qs.stringify(params);

  return axios.request({
    url: '/practice/execCode',
    method: 'post',
    data: req,
  });
};
// 查询全部题目
export const getAllQuestion = () => {
  return axios.request({
    url: '/question/getAllQuestion',
    method: 'get',
  });
};
// 在指定试卷中增加题目
export const addQuestInExer = (params) => {
  var req = qs.stringify(params);
  return axios.request({
    url: '/questinexer/addQuestion',
    method: 'post',
    data: req,
  });
};
// 查询全部试卷
export const getAllExer = (param) => {
  var temp = qs.stringify(param);
  return axios.request({
    url: '/exercise/getAllExercise?' + temp,
    method: 'get',
  });
};
// 查询学生全部实践数据库
export const getStuDb = () => {
  
  return axios.request({
    url: '/database/getUseForStuDatabases',
    method: 'get',
    data: {},
  });
};


// 根据ID获取单个学生信息
export const getOneStudentById = (param) => {
  var query = qs.stringify(param);
  return axios.request({
    url: '/student/getStudentById?' + query,
    method: 'get',
  });
};
// 根据ID获取单个学生信息(模糊匹配)
export const getStudentById = (param) => {
  var query = qs.stringify(param);
  return axios.request({
    url: '/student/getStudentsById?' + query,
    method: 'get',
  });
};
// 根据姓名获取单个学生信息
export const getStudentByName = (param) => {
  var query = qs.stringify(param);
  return axios.request({
    url: '/student/getStudentByName?' + query,
    method: 'get',
  });
};
// 根据姓名或id模糊匹配
export const getStudentByIdOrName = (param) => {
  var query = qs.stringify(param);
  return axios.request({
    url: '/student/getStudentByNameOrId?' + query,
    method: 'get',
    
  });
};
// 修改序号
export const modifyQuestionOrder = (param) => {
  return axios.request({
    url: '/questinexer/modifyExerOrder',
    method: 'post',
    data: param,
    contentType: 'application/json',
  });
};

// 查询指定试题内容
export const getQuestionTeacher = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/question/getQuestionTeacher?'+temp,
    method: 'get',
    data:{}
  })
}
// 增加新试题
export const addNewQuestion = (params) => {
    
  var req=qs.stringify(params)
  return axios.request({
    url:'/question/addNewQuestion',
    method: 'post',
    data:params,
    contentType: 'application/json',
  })
}
// 修改试题
export const modifyQuestion = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/question/modifyQuestion ',
    method: 'post',
    data:req
  })
}
// 查询指定教师全部答题数据库
export const getTeacherAllJudge = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/database/getJudgeDatabasesByteacherId?'+temp,
    method: 'get',
    data:{}
  })
}
// 查询指定试卷全部试题
export const getExerAllContent = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/questinexer/getExerAllContent?'+temp,
    method: 'get',
    data:{}
  })
}
// 设置试卷开放
export const setExerPublic = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/exercise/setExercisePublic',
    method: 'post',
    data:req
  })
}
// 设置试卷私有
export const setExerPrivate = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/exercise/setExercisePrivate',
    method: 'post',
    data:req
  })
}
// 增加试卷
export const addExercise = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/exercise/addExercise ',
    method: 'post',
    data:req
  })
}
// 在指定试卷修改题目分数
export const modifyExerQuestScore = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/questinexer/modifyExerScore',
    method: 'post',
    data:req
  })
}
// 在指定试卷中删除指定题目
export const delExerQuest = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/questinexer/delQuestion  ',
    method: 'post',
    data:req
  })
}
// 增加班级试卷对应关系
export const addClassExer = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/exerinclass/addExerInClass  ',
    method: 'post',
    data:req
  })
}
// 删除班级试卷对应关系
export const delClassExer = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/exerinclass/delExerInClass',
    method: 'post',
    data:req
  })
}
// 查询教师发布的所有班级试卷关系
export const getTeacherAllExerInClass = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/exerinclass/getALLExerInClassByTeacherId?'+temp,
    method: 'get',
    data:{}
  })
}

// 查询使用某试卷的班级
export const getExerUsedClass = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/exerinclass/getExerUsedClass?'+temp,
    method: 'get',
    data:{}
  })
}
// 查询某班级全部试卷
export const getClassAllExer = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/exerinclass/getClassAllExer?'+temp,
    method: 'get',
    data:{}
  })
}
// 查询当前试题所在试卷
export const getExerByQuest = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/questinexer/getExercisesByQuestionId?'+temp,
    method: 'get',
    data:{}
  })
}
// 查询教师负责全部班级
export const getTeacherAllClass = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/stuclass/getStuClassByTeacherId?'+temp,
    method: 'get',
    data:{}
  })
}
// 增加班级
export const addClass = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/stuclass/addStuClass  ',
    method: 'post',
    data:req
  })
}
// 删除班级
export const delClass = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/stuclass/deleteStuClass',
    method: 'post',
    data:req
  })
}
// 禁用班级全部学生账号
export const forbidClassStudent = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/student/forbidClassStudent',
    method: 'post',
    data:req
  })
}
// 激活班级全部学生账号
export const activeClassStudent = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/student/activateClassStudent',
    method: 'post',
    data:req
  })
}
// 删除班级全部学生账号
export const delClassStudent = (params) => {
  var req=qs.stringify(params)
  return axios.request({
    url:'/student/deleteClassStudent',
    method: 'post',
    data:req
  })
}
// 查询指定班级全部学生信息
export const getClassAllStudent = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/student/getStudentByClassId?'+temp,
    method: 'get',
    data:{}
  })
}
// 查询指定教师全部学生信息
export const getTeacherAllStudent = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/student/getStudentByTeacherId?'+temp,
    method: 'get',
    data:{}
  })
}
// 添加学生
export const addStudent = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/student/addStudent',
    method: 'post',
    data:req
  })
}
// 删除学生(设置未激活)
export const delStudent = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/student/forbidStudent',
    method: 'post',
    data:req
  })
}
// 激活学生
export const setStudentActive = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/student/activateStudent',
    method: 'post',
    data:req
  })
}
// 查询某学生某套试卷作答结果
export const getStudentExerAnswer = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/stuanswer/getStuTheExerAnswer?'+temp,
    method: 'get',
    data:{}
  })
}
// 查询某学生全部试卷作答结果
export const getStudentAllAnswer = (param) => {
  var temp=qs.stringify(param)
  return axios.request({
    url:'/stuanswer/getStuAllExerAnswer?'+temp,
    method: 'get',
    data:{}
  })
}
// 修改学生作答分数
export const modifyStudentScore = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/stuanswer/modifyStuScore',
    method: 'post',
    data:req
  })
}
// 修改学生作答结果
export const modifyStudentAnswer = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/stuanswer/modifyStuAnswer',
    method: 'post',
    data:req
  })
}
// 查询全部教师
export const getTeachers = () => {
  
  return axios.request({
    url:'/teacher/getAllTeachers',
    method: 'get',
    data:{}
  })
}
// 创建教师用户
export const addNewTeacher = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/teacher/addTeacher',
    method: 'post',
    data:req
  })
}
// 物理删除教师用户
export const delTeacher = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/teacher/deleteTeacher',
    method: 'post',
    data:req
  })
}
// 查询全部学生
export const getStudents = () => {
  
  return axios.request({
    url:'/student/getAllStudents',
    method: 'get',
    data:{}
  })
}
// 物理删除学生用户
export const realDelStudent = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/student/deleteStudent',
    method: 'post',
    data:req
  })
}
// 授予学生教师权限
export const grandStudentTeacher = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/system/grantTeacher',
    method: 'post',
    data:req
  })
}
// 查询系统当前数据库连接
export const getDbConnectProps = () => {
  
  return axios.request({
    url:'/system/getConnectProperties',
    method: 'get',
    data:{}
  })
}
// 修改当前系统数据库连接
export const setDbConnectProps = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/system/setConnectProperties',
    method: 'post',
    data:req
  })
}
// 重置当前系统数据库连接
export const refreshDb = () => {
  
  return axios.request({
    url:'/system/refresh',
    method: 'post',
    data:{}
  })
}
// 查询全部试卷(包括已删除)
export const getAllExercises = () => {
  
  return axios.request({
    url:'/exercise/getExercises',
    method: 'get',
    data:{}
  })
}
// 逻辑删除试卷
export const delExer = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/exercise/delExercise',
    method: 'post',
    data:req
  })
}
// 物理删除试卷
export const realDelExer = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/exercise/delExercisePhysically',
    method: 'post',
    data:req
  })
}
// 查询全部题目(包括已删除)
export const getAllQuestions = () => {
  
  return axios.request({
    url:'/question/getQuestions',
    method: 'get',
    data:{}
  })
}
// 逻辑删除题目
export const delQuest = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/question/delQuestion',
    method: 'post',
    data:req
  })
}
// 物理删除题目
export const realDelQuest = (param) => {
  var req=qs.stringify(param)
  return axios.request({
    url:'/question/deleteQuestionPhysically',
    method: 'post',
    data:req
  })
}

// 查询教师全部数据库
export const getDatabasesByteacherId = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/database/getDatabasesByteacherId'+'?'+req,
    method: 'get',
  })
}

// 查询教师全部答题数据库
export const getJudgeDatabasesByteacherId = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/database/getJudgeDatabasesByteacherId'+'?'+req,
    method: 'get',
  })
}

// 创建实践数据库
export const addDatabase = (params) => {
  let req=qs.stringify(params)

  return axios.request({
    url:'/database/addDatabase',
    method: 'post',
    data:req
  })
}

// 删除实践数据库
export const deleteDatabase = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/database/deleteDatabaseByName',
    method: 'post',
    data:req
  })
}
// 删除答题数据库
export const deleteJudgeDatabase = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/database/deleteJudgeDatabaseByName',
    method: 'post',
    data:req
  })
}
// 修改答题数据库说明
export const modifyJudgeDatabase = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/database/modifyJudgeDatabase',
    method: 'post',
    data:req
  })
}
// 将实践数据库转为答题数据库
export const transferJudge = (params) =>{
  let req = qs.stringify(params)

  return axios.request({
    url: '/database/transferJudge',
    method: 'post',
    data: req
  })
}
// 批量禁用
export const someStudentsForbid = (param) => {
  return axios.request({
    url: '/student/forbidSomeStudents',
    method: 'post',
    data: param,
    contentType: 'application/json',
  });
};
// 批量激活
export const someStudentsActive = (param) => {
  return axios.request({
    url: '/student/activateSomeStudents',
    method: 'post',
    data: param,
    contentType: 'application/json',
  });
};
// 批量删除
export const someStudentsDelete = (param) => {
  return axios.request({
    url: '/student/deleteSomeStudents',
    method: 'post',
    data: param,
    contentType: 'application/json',
  });
};
// 查询某套试卷总分
export const getExerSumScore = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/questinexer/exerSumScore'+'?'+req,
    method: 'get',
  })
}
// 初始化学生密码
export const initUserPwd = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/user/initializePwd'+'?'+req,
    method: 'get',
  })
}
// 判断文件格式
export const checkFileEncoder = (params) => {
  
  return axios.request({
    url:'/practice/judgefileEncoder',
    method: 'post',
    data:params,
    contentType: 'multipart/form-data'
  })
}
// 用户退出登录
export const userLogout = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/user/logout'+'?'+req,
    method: 'get',
  })
}
// 刷新用户登陆时间
export const logUpdate = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/user/logupdate'+'?'+req,
    method: 'get',
  })
}
// 获取带总分的班级全部试卷
export const getClassAllExers = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/exerinclass/getClassAllExers?'+req,
    method: 'get',
    data:{}
  })
}
// 获取带得分的试卷试题信息
export const getExerAllContents = (param) => {
  let req=qs.stringify(param)

  return axios.request({
    url:'/questinexer/getExerAllContents?'+req,
    method: 'get',
    data:{}
  })
}