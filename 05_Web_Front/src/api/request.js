import axios from 'axios'
import {
	Message
} from 'element-ui'
const baseURL = `${process.env.VUE_APP_API_URL}/`
const service = axios.create({
	baseURL,
	timeout: 100000,
	headers: {
		'Content-Type': 'application/x-www-form-urlencoded',
		'lang': 'en_US'
	}
})

service.interceptors.request.use(config => {
	let token = localStorage.getItem('token')
	let lang = localStorage.getItem('lang')
	if (token) {
		config.headers['x-auth-token'] = token 
	}
	if (lang) {
		config.headers['lang'] = lang
	}
	return config
}, error => {
	Promise.reject(error)
})

service.interceptors.response.use(
	response => {
		const res = response.data
		if (res.code == 4000) {
			window.location.href = "#/login";
		}
		return res;
	},
	error => {
		Message({
			message: error.message,
			type: 'error',
			duration: 3000
		})
		return Promise.reject(error)
	})

export default service
