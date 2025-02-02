import { defineStore } from 'pinia';

const ENUM_ROLE = {
	"customer" : "CUSTOMER",
	"landlord" : "LANDLORD"
}

export const useSignupStore = defineStore('signup', {
	state: () => ({
		uuid: null,
		email: "",
		password: "",
		name: "",
		role: null,
		social: false,
		business: "",
	}),
	actions: {
		setUuid(uuid) {
			this.uuid = uuid;
		},
		setEmail(email) {
			this.email = email;
		},
		setPassword(password) {
			this.password = password;
		},
		setName(name) {
			this.name = name;
		},
		setCustomer() {
			this.role = ENUM_ROLE.customer;
		},
		setLandlord() {
			this.role = ENUM_ROLE.landlord;
		},
		useSocial() {
			this.social = true;
		},
		useEmail() {
			this.social = false;
		},
		setBusiness(business) {
			this.business = business;
		},
		resetState() {
			this.uuid = null;
			this.email = "";
			this.password = "";
			this.name = "";
			this.role = null;
			this.social = false;
			this.business = "";
		},
	},
	getters: {
		isCustomer: (state) => state.role === ENUM_ROLE.customer,
		isLandlord: (state) => state.role === ENUM_ROLE.landlord,
		getAllState: (state) => JSON.parse(JSON.stringify(state))
	},
});