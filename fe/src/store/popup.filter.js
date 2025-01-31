import { defineStore } from 'pinia';

export const usePopupFilterStore = defineStore('popupFilter', {
	state: () => ({
		// 지역 필터
		location: "",
		
		// 유형 필터
		type: "",

		// 연령 필터
		age: "",

		// 기간 필터
		start: "",
		end: "",

		// 정렬 필터
		sort: "",
	}),
	actions: {
		setLocation(value) {
			this.location = value;
		},
		setType(value) {
			this.type = value;
		},
		setAge(value) {
			this.age = value;
		},
		setPeriod(period) {
			if (Array.isArray(period) && period.length === 2) {
				this.start = period[0];
				this.end = period[1];
			} else {
				console.warn("잘못된 기간 형식:", period);
			}
		},
		setSort(value) {
			this.sort = value;
		},
		setPage(value) {
			this.page = value;
		},

		resetFilters() {
			this.location = "";
			this.type = "";
			this.age = "";
			this.start = "";
			this.end = "";
			this.sort = "";
		}
	},
	getters: {
		period: (state) => {
			if (!state.start && !state.end) return "";
			if (!state.start) return state.end;
			if (!state.end) return state.start;
			return `${state.start} ~ ${state.end}`;
		},
	}
});