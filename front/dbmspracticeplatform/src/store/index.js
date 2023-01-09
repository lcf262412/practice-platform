import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
  // vuex状态通过sessionStorage传递，这样页面刷新状态不丢失
  state: {
    role: sessionStorage.getItem('role'),
    username: sessionStorage.getItem('username'),isLoading:false
  },
  mutations: {
    resetState: (state) => {
      Object.assign(state, getDefaultState());
    },
    setRole(state, data) {
      state.role = data;
      sessionStorage.setItem('role', data);
    },
    setUsername(state, data) {
      state.username = data;
      sessionStorage.setItem('username', data);
      
    },
    setLoading(state, isLoading) {
      state.isLoading = isLoading;
            
    }
  },
  actions: {
        
    onLoading(context, isLoading) {
      context.commit('setLoading', isLoading);
    }
          
  },
  getters: {},
});

const getDefaultState = () => {
  return {
    role: '',
    username: '',
  };
};
