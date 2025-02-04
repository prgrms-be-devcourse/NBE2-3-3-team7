<script setup>
import { computed, onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { userHome } from '@/services/user/user.api';
import LandlordDashboard from '@/components/user/home/LandlordDashboard.vue';
import CustomerDashboard from '@/components/user/home/CustomerDashboard.vue';
import AdminDashboard from '@/components/user/home/AdminDashboard.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';

const data = ref(null);
const isLoading = ref(true);

const fetchUserHome = async () => {
	try {
		const result = await userHome();
		data.value = result;
		console.log(result);
	} catch (err) {
		console.error('API 요청 오류:', err);
	} finally {
		isLoading.value = false;
	}
};


onMounted(() => {
	fetchUserHome();
});


const userRole = computed(() => data.value?.user?.role);
const landlordData = computed(() => ({
	land: data.value?.land || [],
	analytics: data.value?.analytics || []
}));
const customerData = computed(() => ({
	popup: data.value?.popup || [],
	payment: data.value?.payment || []
}));

</script>

<template>
	<LoadingSpinner v-if="isLoading" text="로딩 중 . . ." />
	<main v-else class="flex-grow flex flex-col items-center">
		<section class="flex w-full py-10 border-b bg-white justify-center">
			<div class="flex justify-center w-full items-center space-x-20">
				<div class="flex flex-col justify-center items-center space-y-4">
					<img :src="data.user?.profileImage"
						class="rounded-full w-40 h-40 border border-gray-300 object-cover bg-white" alt="">
					<router-link to="/user/edit"
						class="text-white bg-[#3FB8AF] hover:bg-[#2c817c] p-2 rounded-md transition-colors text-sm font-normal mt-35">
						회원정보 수정하기
					</router-link>
				</div>
				<div class="flex flex-col h-full justify-between space-y-4">
					<span class="h-9 block p-1 font-bold text-gray-700 flex-shrink-0">
						이름 : {{ data.user?.name }}
					</span>
					<span class="h-9 block p-1 font-bold text-gray-700 flex-shrink-0">
						이메일 : {{ data.user?.email }}
					</span>
					<span class="h-9 block p-1 font-bold text-gray-700 flex-shrink-0">
						전화번호 : {{ data.user?.tel }}
					</span>
					<span class="h-9 block p-1 font-bold text-gray-700 flex-shrink-0">
						{{ data.user?.role === 'LANDLORD' ? `사업자등록번호 : ${data.user?.businessId}` : '' }}
					</span>
				</div>
			</div>
		</section>

		<LandlordDashboard v-if="userRole === 'LANDLORD'" :land="landlordData.land"
			:analytics="landlordData.analytics" />
		<CustomerDashboard v-else-if="userRole === 'CUSTOMER'" :popup="customerData.popup"
			:payment="customerData.payment" />
		<AdminDashboard v-else-if="userRole === 'ADMIN'" />
	</main>
</template>