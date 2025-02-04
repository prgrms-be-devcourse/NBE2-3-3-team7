<script setup>
import MobileModal from './MobileModal.vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { ref, watch } from 'vue';
import { useAuthStore } from '@/store/auth';

const modal = ref(false);
const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const handleModal = () => {
	modal.value = !modal.value;
};

const handleSignout = async() => {
	await authStore.logout();
	router.push('/');
};

watch(route, () => {
	modal.value = false;
});
</script>

<template>
	<header class="bg-white border-b-2">
		<nav class="mx-auto flex max-w-7xl items-center justify-between p-6 lg:px-8" aria-label="Global">
			<div class="flex lg:flex-1">
				<router-link to="/" class="-m-1.5 p-1.5">
					<img class="h-16 w-auto" src="../../assets/logo.png" alt="">
				</router-link>
			</div>

			<div class="flex lg:hidden">
				<button @click="handleModal" type="button"
					class="-m-2.5 inline-flex items-center justify-center rounded-md p-2.5 text-gray-700">
					<div class="size-6">
						<i class="fas fa-bars"></i>
					</div>
				</button>
			</div>

			<div class="hidden lg:flex lg:gap-x-12">
				<router-link to="/land"
					class="text-xl font-semibold p-4 rounded-lg hover:bg-gray-200 transition-colors hover:text-[#3FB8AF] text-gray-900">임대지</router-link>
				<router-link to="/popup"
					class="text-xl font-semibold p-4 rounded-lg hover:bg-gray-200 transition-colors hover:text-[#3FB8AF] text-gray-900">팝업</router-link>
				<router-link to="/user" v-if="authStore.isLoggedIn"
					class="text-xl font-semibold p-4 rounded-lg hover:bg-gray-200 transition-colors hover:text-[#3FB8AF] text-gray-900">회원</router-link>
			</div>

			<div class="hidden lg:flex lg:flex-1 lg:justify-end">
				<router-link v-if="!authStore.isLoggedIn" to="/signin"
					class="text-xl/6 font-semibold p-4 rounded-lg hover:bg-slate-200 transition-colors hover:text-[#3FB8AF] text-gray-900">
					Sign in <span aria-hidden="true">&rarr;</span>
				</router-link>
				<button v-else @click="handleSignout"
					class="text-xl/6 font-semibold p-4 rounded-lg hover:bg-slate-200 transition-colors hover:text-[#3FB8AF] text-gray-900">
					Sign out <span aria-hidden="true">&rarr;</span>
				</button>
			</div>
		</nav>
		<MobileModal v-if="modal" @close="handleModal"/>
	</header>
</template>

<style scoped></style>
