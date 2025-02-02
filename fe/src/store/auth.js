import { defineStore } from 'pinia';
import { signout, signinEmail } from '@/services/user/auth/sign.api';
import { getUserInfo } from '@/services/user/user.api';

export const useAuthStore = defineStore('auth', {
	state: () => ({
		isLoggedIn: !!localStorage.getItem('token'),
		user: null,
	}),
	actions: {
		async login(email, password) {
			try {
				const response = await signinEmail({ email: email.value, password: password.value });
				localStorage.setItem('token', response.accessToken);
				this.isLoggedIn = true;
			} catch (err) {
				console.error(err);
			}
		},

		async fetchUser() {
			try {
				const response = await getUserInfo();
				this.user = response;
				this.isLoggedIn = true;
			} catch (error) {
				console.error('사용자 정보 가져오기 실패:', error);
				this.user = null;
				this.isLoggedIn = false;
			}
		},

		async logout() {
			try {
				await signout();
				localStorage.removeItem('token');
				this.isLoggedIn = false;
				this.user = null;
			} catch (err) {
				console.error('API 요청 오류:', err);
			}
		},

		checkLoginStatus() {
			this.isLoggedIn = !!localStorage.getItem('token');
		},
	}
});
