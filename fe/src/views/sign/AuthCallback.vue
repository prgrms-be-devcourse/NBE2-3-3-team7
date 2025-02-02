<script setup>
import { useRouter, useRoute } from 'vue-router';
import { useSignupStore } from '@/store/signup';

const router = useRouter();
const route = useRoute();

const signupStore = useSignupStore();

const token = route.query.token;
const email = route.query.email;
const name = route.query.name;
const uuid = route.query.uuid;

if (token) {
	localStorage.setItem("token", token);
	router.push("/");
} else if (uuid && email && name) {
	signupStore.setUuid(uuid);
	signupStore.setEmail(email);
	signupStore.setName(name);
	signupStore.useSocial();
	router.push('/signup/role');
} else {
	// router.push("/signin");
}
</script>

<template>
	<div class="flex justify-center items-center h-screen bg-gray-100">
		<div class="text-center">
			<div class="w-12 h-12 border-4 border-t-[#3FB8AF] border-gray-300 rounded-full animate-spin mx-auto"></div>
			<p class="mt-4 text-lg font-semibold text-gray-700">로그인 처리 중...</p>
		</div>
	</div>
</template>

<style scoped></style>