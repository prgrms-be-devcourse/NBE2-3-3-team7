<script setup>
import { BarChart } from 'vue-chart-3';
import { Chart, registerables } from 'chart.js';
import { formattedKorWon, getStatusClass } from '@/utils/global.util';
import { adminDashboard } from '@/services/admin/admin.api';
import { onMounted, ref } from 'vue';

Chart.register(...registerables);

const info = ref(null);
const totalUser = ref(0);
const totalLand = ref(0);
const totalPopup = ref(0);

const formatDate = (dateString) => {
	// eslint-disable-next-line no-unused-vars
	const [year, month, day] = dateString.split("-");
	return `${month}.${day}`;
};

const userChartData = ref({});

const chartOptions = {
	responsive: true,
	maintainAspectRatio: false,
	plugins: {
		legend: {
			position: "bottom",
		},
		tooltip: {
			enabled: true,
		},
	},
};



onMounted(async () => {
	await fetchDashboard()
	totalUser.value = info.value.countUsers.CUSTOMER + info.value.countUsers.LANDLORD + info.value.countUsers.ADMIN
	totalLand.value = (info.value.totalLands.ACTIVE || 0) + (info.value.totalLands.INACTIVE || 0)
	totalPopup.value = (info.value.totalPopups.ACTIVE || 0) + (info.value.totalPopups.INACTIVE || 0)
	userChartData.value = {
		labels: info.value?.dailyCounts.map((item) => formatDate(item.date)),
		datasets: [
			{
				label: "일일 가입자",
				data: info.value?.weeklyRegistered.map((item) => item.count),
				backgroundColor: "#3FB8AF",
			},
			{
				label: "일일 거래량",
				data: info.value?.dailyCounts.map((item) => item.count),
				backgroundColor: "#2c817c",
			}
		],
	}
})

const fetchDashboard = async () => {
	const response = await adminDashboard();
	info.value = response;
	console.log(response);
}

</script>

<template>
	<main class="p-6 flex-1 space-y-6 overflow-auto height_scrollbar mx-2 max-w-7xl w-full justify-center items-center">
		<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
			<div class="relative bg-white border border-gray-200 shadow rounded-lg py-6 text-center group">
				<div class="font-bold">
					<span class="text-gray-700"><i class="fas fa-users"></i> 총 회원 수</span>
					<h1 class="text-4xl my-3 text-gray-500">{{ totalUser }}명</h1>
				</div>
				<div
					class="text-gray-500 absolute inset-0 font-bold bg-white bg-opacity-85 flex flex-col justify-center items-center rounded-lg opacity-0 group-hover:opacity-100 transition-opacity">
					<p class="flex w-full justify-between px-6">사용자 <span>{{ info?.countUsers.CUSTOMER }}</span></p>
					<p class="flex w-full justify-between px-6">임대인 <span>{{ info?.countUsers.LANDLORD }}</span></p>
					<p class="flex w-full justify-between px-6">관리자 <span>{{ info?.countUsers.ADMIN }}</span></p>
				</div>
			</div>
			<div class="relative bg-white border border-gray-200 shadow rounded-lg py-6 text-center group">
				<div class="font-bold">
					<span class="text-gray-700"><i class="fas fa-money-bill-wave"></i> 월별 거래 현황</span>
					<h1 class="text-4xl my-3 text-gray-500">{{ info?.monthlyCount.monthlyCount }}회</h1>
				</div>
				<div
					class="text-gray-500 absolute inset-0 font-bold bg-white bg-opacity-85 flex flex-col justify-center items-center rounded-lg opacity-0 group-hover:opacity-100 transition-opacity">
					<p class="flex w-full justify-between px-6">월간 수익 <span>{{
						formattedKorWon(info?.monthlyCount.monthly) }}</span></p>
					<p class="flex w-full justify-between px-6">주간 수익 <span>{{
						formattedKorWon(info?.monthlyCount.weekly) }}</span></p>
					<p class="flex w-full justify-between px-6">일간간 수익 <span>{{
						formattedKorWon(info?.monthlyCount.daily) }}</span></p>
				</div>
			</div>

			<div class="relative bg-white border border-gray-200 shadow rounded-lg py-6 text-center group">
				<div class="font-bold">
					<span class="text-gray-700"><i class="fas fa-building"></i> 총 임대지 현황</span>
					<h1 class="text-4xl my-3 text-gray-500">{{ totalLand }}곳</h1>
				</div>
				<div
					class="text-gray-500 absolute inset-0 font-bold bg-white bg-opacity-85 flex flex-col justify-center items-center rounded-xl opacity-0 group-hover:opacity-100 transition-opacity">
					<p class="flex w-full justify-between px-6">활성화 <span>{{ info?.totalLands.ACTIVE || 0 }}</span></p>
					<p class="flex w-full justify-between px-6">비활성화 <span>{{ info?.totalLands.INACTIVE || 0 }}</span>
					</p>
				</div>
			</div>
			<div class="relative bg-white border border-gray-200 shadow rounded-lg py-6 text-center group">
				<div class="font-bold">
					<span class="text-gray-700"><i class="fas fa-store"></i> 총 팝업 현황</span>
					<h1 class="text-4xl my-3 text-gray-500">{{ totalPopup }}곳</h1>
				</div>
				<div
					class="text-gray-500 absolute inset-0 font-bold bg-white bg-opacity-85 flex flex-col justify-center items-center rounded-xl opacity-0 group-hover:opacity-100 transition-opacity">
					<p class="flex w-full justify-between px-6">활성화 <span>{{ info?.totalPopups.ACTIVE || 0 }}</span></p>
					<p class="flex w-full justify-between px-6">비활성화 <span>{{ info?.totalPopups.INACTIVE || 0 }}</span>
					</p>
				</div>
			</div>
		</div>

		<div class="grid grid-cols-1 gap-6">
			<div class="bg-white shadow border border-gray-200 rounded-lg p-4">
				<h2 class="text-gray-500 font-bold mb-4">일별 신규 가입자</h2>
				<div class="flex w-full">
					<BarChart :chartData="userChartData" :options="chartOptions" class="w-full" />
				</div>
			</div>
		</div>

		<div class="bg-white shadow border border-gray-200 rounded-lg p-4">
			<h2 class="text-gray-500 font-bold mb-4">최근 거래 내역</h2>
			<table class="w-full text-left">
				<thead>
					<tr class="text-gray-500">
						<th class="p-2">고객명</th>
						<th class="p-2">임대인명</th>
						<th class="p-2">결제 금액</th>
						<th class="p-2">임대 기간</th>
						<th class="p-2">결제 상태</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="(item, index) in info?.dashboardList" :key="index"
						class="hover:bg-gray-100 border-t border-gray-300">
						<td class="p-2">{{ item.customer }}</td>
						<td class="p-2">{{ item.landlord }}</td>
						<td class="p-2">{{ formattedKorWon(item.receipts.amount) }}</td>
						<td class="p-2">{{ item.receipts.start }}<span class="text-xs"> ~ </span>{{ item.receipts.end }}
						</td>
						<td class="p-2 font-bold" :class="getStatusClass(item.receipts.status)">{{ item.receipts.status
							}}</td>
					</tr>
				</tbody>
			</table>
		</div>
	</main>

</template>

<style scoped></style>
