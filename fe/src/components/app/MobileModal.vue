<script setup>
import { RouterLink, useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const emit = defineEmits(['close']);
const authStore = useAuthStore();
const router = useRouter();

const handleSignout = async() => {
	emit('close');
	await authStore.logout();
	router.push('/');
};
</script>

<template>
	<div id="mobile-modal" role="dialog" aria-modal="true">
		<div @click="emit('close')" class="fixed inset-0 z-20"></div>
		<div
			class="fixed inset-y-0 right-0 z-20 w-full overflow-y-auto bg-white px-6 py-6 sm:max-w-sm sm:ring-1 sm:ring-gray-900/10">
			<div class="flex items-center justify-between">

				<router-link to="/" class="-m-1.5 p-1.5">
					<img class="h-16 w-auto" src="@/assets/logo.png" alt="">
				</router-link>
				<button @click="emit('close')" type="button" class="-m-2.5 rounded-md p-2.5 text-gray-700">
					<div class="size-6">
						<i class="fas fa-xmark"></i>
					</div>
				</button>
			</div>
			<div class="mt-6 flow-root">
				<div class="-my-6 divide-y divide-gray-500/10">
					<div class="space-y-2 py-6">
						<router-link to="/land"
							class="-mx-3 block rounded-lg px-3 py-2 text-base/7 font-semibold text-gray-900 hover:bg-gray-200 transition-colors hover:text-[#3FB8AF]">임대지</router-link>
						<router-link to="/popup"
							class="-mx-3 block rounded-lg px-3 py-2 text-base/7 font-semibold text-gray-900 hover:bg-gray-200 transition-colors hover:text-[#3FB8AF]">팝업</router-link>
						<router-link to="/user" v-if="authStore.isLoggedIn"
							class="-mx-3 block rounded-lg px-3 py-2 text-base/7 font-semibold text-gray-900 hover:bg-gray-200 transition-colors hover:text-[#3FB8AF]">회원</router-link>
					</div>
					<div class="py-6">
						<router-link v-if="!authStore.isLoggedIn" to="/signin"
							class="-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold text-gray-900 hover:bg-gray-200 transition-colors hover:text-[#3FB8AF]">
							Sign in <span aria-hidden="true">&rarr;</span>
						</router-link>
						<button v-else @click="handleSignout"
							class="-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold text-gray-900 hover:bg-gray-200 transition-colors hover:text-[#3FB8AF]">
							Sign out <span aria-hidden="true">&rarr;</span>
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped></style>
