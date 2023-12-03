import Vue from 'vue'
import Vuex from 'vuex'
import persistedState from "vuex-persistedstate";

Vue.use(Vuex)

const store = new Vuex.Store({
	state: {
		theme: "white",
		lang: "en_US",
		user: {},
		isLogin: false,
		member: null,
		payInfo: {},
		current: {
			fullName: "USD",
			symbol: "$",
			rate: 1
		}
	},
	mutations: {
		SET_THEME(state, value) {
			state.theme = value
		},
		SET_USER(state, value) {
			state.user = value
		},
		SET_ISLOGIN(state, value) {
			state.isLogin = value
		},
		SET_LANG(state, value) {
			state.lang = value
		},
		SET_PAYINFO(state, value) {
			state.payInfo = value
		},
		SET_MEMBER(state, member) {
			state.member = member;
			localStorage.setItem('MEMBER', JSON.stringify(member));
		},
		SET_CURRENT(state, current) {
			state.current = current;
		},
	},
	actions: {
		setTheme({
			commit
		}, value) {
			commit('SET_THEME', value)
			localStorage.setItem('theme', value)
		},
		setUser({
			commit
		}, value) {
			commit('SET_USER', value)
		},
		setLang({
			commit
		}, value) {
			commit('SET_LANG', value)
			localStorage.setItem('lang', value)
		},
		setCurrent({
			commit
		}, value) {
			commit('SET_CURRENT', value)
		}
	},
	getters: {
		member(state) {
			return state.member;
		},
		isLogin(state) {
			return state.isLogin;
		},
		current(state) {
			return state.current;
		},
		theme(state) {
			return state.state;
		}
	},
	plugins: [
		persistedState({
			storage: window.sessionStorage,
			reducer(val) {
				return {
					user: val.user,
					isLogin: val.isLogin,
					payInfo: val.payInfo,
					member: val.member,
					current: val.current
				}
			}
		})
	]
})

export default store;
