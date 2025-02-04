import { defineStore } from 'pinia';

export const useLandFilterStore = defineStore('landFilter', {
	state: () => ({
		// 가격 필터
		minPrice: 0,
		maxPrice: 10000000,

		// 면적 필터
		minArea: 0,
		maxArea: 200,

		// 기간 필터
		start: "",
		end: "",

		// 위치 필터
		location: "",

		// 정렬 필터
		sort: ""
	}),
	actions: {
		setPriceRange(min, max) {
			this.minPrice = min;
			this.maxPrice = max;
		},

		setAreaRange(min, max) {
			this.minArea = min;
			this.maxArea = max;
		},

		setPeriod(period) {
			if (Array.isArray(period) && period.length === 2) {
				this.start = period[0];
				this.end = period[1];
			} else {
				console.warn("잘못된 기간 형식:", period);
			}
		},

		setLocation(loc) {
			this.location = loc;
		},

		setSort(sort) {
			this.sort = sort;
		},

		resetFilters() {
			this.minPrice = 0;
			this.maxPrice = 10000000;
			this.minArea = 0;
			this.maxArea = 200;
			this.start = "";
			this.end = "";
			this.location = "";
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