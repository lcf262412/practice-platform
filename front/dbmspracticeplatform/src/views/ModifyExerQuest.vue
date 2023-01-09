<template><!--修改试卷内容页面-->
  <el-container class="paper-page">
    <el-header>
            <common-header></common-header>
        </el-header>
    
    <div>
        <el-header class="paper-header">
            
            <div class="page-title3"><h1>{{this.$route.query.exerciseName}} No:{{this.$route.query.exerciseId}}</h1>   
            </div>
            <el-button class="publishExer" type="primary" size="medium" @click="returnExercise"  >返回试卷列表</el-button>
            <el-button class="addQuestion" type="text" size="medium" @click="modifyOrderClick"  ><h1>确认序号修改</h1></el-button>
            <el-button class="addQuestion" type="text" size="medium" @click="jumpToAdd"  ><h1>添加试题</h1></el-button>
            
        </el-header>
        
    </div>
      
    
    <el-main class="paper-main">
      <!-- 静态生成表格列 -->
      <el-table
        :header-cell-style="{
          fontSize: '18px'
        }" 
        :cell-style="{
        fontFamily: 'MicroSoft YaHei',
        fontSize: '18px', 
        }"
        :key="itemkey"
        :data="tableData" 
         
        style="width: 100%"
        
      >
        <el-table-column prop="orderId" label="试题序号" width="120">
        </el-table-column>
        <el-table-column prop="questionId" label="试题编号" width="120">
            <template slot-scope="scope">
               <div>
                 <a href="javascript:;" @click="titleClick(scope.row)">{{scope.row.questionId}}</a>
               </div>
             </template>
        </el-table-column>
        
        <el-table-column prop="dbName" label="大题名称(数据库名称)" >
        </el-table-column>
        <el-table-column prop="score" label="分值" width="150px">
            <template slot-scope="scope">
               <div>
                 <a href="javascript:;" @click="jumpToText(scope.row)">{{scope.row.score}}</a>
               </div>
             </template>
        </el-table-column> 
        <el-table-column width="150px" prop="orderId" label="修改序号">
            <template slot-scope="scope">
               <div>
                 <a href="javascript:;" @click="upClick(scope.row,scope.$index)">上移</a>
               </div>
             </template>
        </el-table-column> 
        <el-table-column width="150px" prop="orderId" label="修改序号">
            <template slot-scope="scope">
               <div>
                 <a href="javascript:;" @click="downClick(scope.row,scope.$index)">下移</a>
               </div>
             </template>
        </el-table-column>
        <el-table-column width="60px">
            <template slot-scope="scope">
               <div>
                 <a href="javascript:;" @click="deleteClick(scope.row)">删除</a>
               </div>
             </template>
        </el-table-column>
      </el-table>
       <el-dialog title="修改分数" :visible.sync="dialogFormVisible">
                    <el-form :model="req">
                        <el-form-item label="修改题目分数" :label-width="formLabelWidth">
                             <el-input v-model="req.score" placeholder="请输入分数，在0-100之间"  @change="inputScore($event)"></el-input>
                        </el-form-item>
                    </el-form>
                    <div slot="footer"  class="dialog-footer">
                        <el-button @click="dialogFormVisible = false">取 消</el-button>
                        

		                    
                        <el-button type="primary"  @click="modifyClick">确 定</el-button>
                        
                    </div>
             </el-dialog> 
       
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from '../components/CommonHeader.vue'
import { getExerAllContent } from "@/api/api.js"
import { delExerQuest } from "@/api/api.js"
import { modifyExerQuestScore } from "@/api/api.js"
import { modifyQuestionOrder } from "@/api/api.js"


export default {
    components: {
        CommonHeader
    },
  data() {
    return {
      itemkey:true, 
      req:{
        questionId:'',
        exerciseId:'',
        score:'',
      },
      request:{
        exerciseId:this.$route.query.exerciseId
      },
      old_order:[],
      new_order:[],
      allExer:[],
      tableData: [],
      tableLable: {
       questionId:'试题编号',
       orderId:'试题序号',
       questionClass:'试题类别',
       dbName:'大题名称(数据库)',
       title:'试题标题',
       score:'分值',
       order_Id:'修改序号',
      },
      order_Id:'',
      quest_Id:'',
      questOrders:[],
      dialogFormVisible:false,
      form: {
          exercise:'',
          score:''
        },
      
      
      formLabelWidth: '120px',
    };
  },
  created(){
    getExerAllContent(this.request).then(res=>{//获得试卷全部试题信息
      const { code, result } = res.data
      if (code === '0000') {
        this.tableData = result
        this.old_order=result
      }
      
      
      
    })
  },
  mounted() {
    
    
    getExerAllContent(this.request).then(res=>{//获得试卷全部试题信息
      const { code, result } = res.data
      if (code === '0000') {
        this.tableData = result
        
      }
      
     
      
    })
    
  },
  
  methods: {
    
    titleClick (row) {//跳转到试题详情
      
      this.$router.push({ path: '/questionContent', query: { questionId: row.questionId} })
    },
    jumpToAdd(){//跳转到添加试题
      this.$router.push({ path: '/addExerQuest' ,query:{exerciseId:this.$route.query.exerciseId,exerciseName:this.$route.query.exerciseName}})
    },
    jumpToText(row){//弹窗修改分值
        this.dialogFormVisible=true
        this.req.questionId=row.questionId;
        this.req.exerciseId=this.$route.query.exerciseId
        this.req.score=row.score
        
    },
    inputScore(event){//绑定输入分值
        this.req.score=event;
        
    },
    modifyClick(){//修改分值信息
        if(this.req.score<0||this.req.score>100){
          this.$alert('分值不能小于0超过100！')
        }
        else{
          modifyExerQuestScore(this.req).then(res=>{
            const { code, result } = res.data
            if (code === '0000') {
               
            }
            this.dialogFormVisible=false
            this.$router.go(0)
        })
        }
       
    },
    modifyOrderClick(){

      this.new_order=this.tableData
      for(let i in this.old_order){
        if(this.old_order[i].questionId!=this.new_order[i].questionId){
           this.questOrders.push({questionId:this.new_order[i].questionId,orderId:parseInt(i)+1}) 
        }
      }
      modifyQuestionOrder({exerciseId:this.$route.query.exerciseId,questOrders:this.questOrders}).then(res=>{
            const { code, result } = res.data
            if (code === '0000') {
               this.$alert("修改成功")
            }
            else{
              this.$alert("修改失败")
            }
            this.$router.go(0);
      
    })
    },
    upClick(row,index){
       if (index > 0) {
                let upData = this.tableData[index - 1];
                this.tableData.splice(index - 1, 1);
                this.tableData.splice(index, 0, upData);
            } else {
                this.$message({
                    message: '已经是第一条，上移失败',
                    type: 'warning'
                });
            }


    },
    downClick(row,index){
      if ((index + 1) == this.tableData.length) {
                this.$message({
                    message: '已经是最后一条，下移失败',
                    type: 'warning'
                });
            } else {
                let downData = this.tableData[index + 1];
                this.tableData.splice(index + 1, 1);
                this.tableData.splice(index, 0, downData);
            }
    },

    

    deleteClick(row){//删除试题
        
        delExerQuest({questionId:row.questionId,exerciseId:this.$route.query.exerciseId}).then(res=>{
            const { code, result } = res.data
            if (code === '0000') {
               
            }
            this.$router.go(0);
      
    })
    },
    returnExercise(){//返回试卷列表
      this.$router.push({ path: '/exercise'})
    }
    
  }
};
</script>

<style>
.el-header {
    padding: 0;
}
.addQuestion{
    display: flex;
    justify-content: flex-end;
}
.paper-page {
  background-color: #eeeeee;
  padding: 10px;
}
.paper-header {
  background-color: white;
  border-radius: 10px;
  box-shadow: 1px 1px 1px rgb(154, 154, 154);
  display: flex;
  flex-direction: row;
  align-items: center;
}
.page-title3 {
  margin-left: 20px;
  margin-right: auto;
}
.paper-main {
  margin-top: 10px;
  background-color: white;
  border-radius: 10px;
  
  box-shadow: 1px 1px 1px rgb(154, 154, 154);
  
}
</style>
