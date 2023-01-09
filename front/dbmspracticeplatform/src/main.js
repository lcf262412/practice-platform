import Vue from 'vue';
import App from './App.vue';
import Vuex from 'vuex';
import store from '@/store'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

import router from './router';

import http from 'axios';

import '@/api/mock';

Vue.config.productionTip = false;

ElementUI.Dialog.props.closeOnClickModal.default=false
Vue.use(ElementUI);
Vue.use(Vuex);

Vue.prototype.$http = http;

Vue.prototype.global = global;

Vue.prototype.$reset = function (formRef, ...excludeFields) {// 弹窗关闭清空
  this.$refs[formRef].resetFields();
  
};

Vue.prototype.$resetSetItem = function (key, newVal) {
  if (key === 'username') {

       
    var newStorageEvent = document.createEvent('StorageEvent');
    const storage = {
      setItem: function (k, val) {

        // 初始化创建的事件
        newStorageEvent.initStorageEvent('setItem', false, false, k, null, val, null, null);

        // 派发对象
        window.dispatchEvent(newStorageEvent)
            
      }
    }
    return storage.setItem(key, newVal);
  }
  else if(key==='clear'){
    var newStorageEvent = document.createEvent('StorageEvent');
    const storage = {
      clear: function (k, val) {

        // 初始化创建的事件
        newStorageEvent.initStorageEvent('clear', false, false, k, null, val, null, null);

        // 派发对象
        window.dispatchEvent(newStorageEvent)
      }
    }
    return storage.clear(key, newVal);
  }
}
const vm = new Vue({
  store,
  router,
  render: (h) => h(App),
}).$mount('#app');
