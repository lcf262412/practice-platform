import Mock from 'mockjs'
import homeApi from './mockServeData/home.js'

Mock.mock('/home/getData', homeApi.getData)
// 模式内元数据mock
Mock.mock('/home/mockSchemaMetadata', homeApi.mockSchemaMetadata)