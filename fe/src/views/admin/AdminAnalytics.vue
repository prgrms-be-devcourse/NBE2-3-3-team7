<script setup>
import { Chart, registerables } from 'chart.js';
import { BarChart, PieChart } from 'vue-chart-3';
import { ref, computed, onMounted, watch } from 'vue';
import { dailyAnalytics, monthlyAnalytics } from '@/services/admin/admin.api'
import { useRoute } from 'vue-router';

Chart.register(...registerables);

const route = useRoute();
const selectedPeriod = ref("daily");

// ✅ 선택된 옵션 (기간 선택)
const selected = ref();
const data = ref({ current: [], prev: [], data: [] }); // 기본값 설정

// ✅ 옵션 목록 (월별 / 일별)
const options = computed(() =>
	selectedPeriod.value === "monthly"
		? [
			{ label: "3개월", value: 3 },
			{ label: "6개월", value: 6 },
			{ label: "12개월", value: 12 },
		]
		: [
			{ label: "7일", value: 7 },
			{ label: "14일", value: 14 },
			{ label: "28일", value: 28 },
		]
);

// ✅ 옵션이 변경될 때, 기본 선택값 설정
watch(
	options,
	(newOptions) => {
		if (!selected.value || !newOptions.some((opt) => opt.value === selected.value)) {
			selected.value = newOptions[0].value; // 첫 번째 옵션 기본값
		}
	},
	{ immediate: true } // 컴포넌트 로드 시 즉시 실행
);

// ✅ API 데이터 가져오기
const fetchAnalytics = async () => {
	try {
		const params = { ...route.query };

		if (selectedPeriod.value === "daily") {
			params.days = selected.value;
			const result = await dailyAnalytics(params);
			data.value = result;
			console.log("일별 데이터:", result);
		} else {
			params.months = selected.value;
			const result = await monthlyAnalytics(params);
			data.value = result;
			console.log("월별 데이터:", result);
		}
	} catch (err) {
		console.error("API 요청 오류:", err);
	}
};

// ✅ 차트 데이터 변환
const chartData = computed(() => {
	const labels = data.value.current?.map((item) => item.date) || [];

	return {
		labels,
		datasets: [
			{
				label: "현재 매출",
				data: data.value.current?.map((item) => item.revenue) || [], // 매출 데이터
				backgroundColor: "#3FB8AF",
			},
			{
				label: "이전 매출",
				data: data.value.prev?.map((item) => item.revenue) || [], // 이전 매출 데이터
				backgroundColor: "#2c817c",
			},
		],
	};
});

const locationData = computed(() => ({
	labels: data.value.data.labels, // X축 (지역명)
	datasets: [
		{
			label: "지역별 매출",
			data: data.value.data.data, // Y축 (매출 금액)
			backgroundColor: [
				"#FF6384",
				"#36A2EB",
				"#FFCE56",
				"#4BC0C0",
			], // 색상 지정
		},
	],
}));

// ✅ 차트 옵션 설정
const chartOptions = {
	responsive: true,
	maintainAspectRatio: false,
	plugins: {
		legend: {
			position: "bottom",
		},
	},
};

watch([selectedPeriod, selected], fetchAnalytics, { immediate: true });

// ✅ API 호출 (컴포넌트 로드 시 실행)
onMounted(async () => {
	await fetchAnalytics();
});
</script>

<template>
	<main class="flex-col flex-1 overflow-auto height_scrollbar w-full max-w-6xl p-6">
		<div class="flex justify-start items-center mb-6">
			<h1 class="text-2xl font-bold text-gray-700">데이터 분석 및 통계</h1>
		</div>
		<div class="flex items-center space-x-4 py-2">
			<div class="flex space-x-2 p-2 border rounded-md h-20">
				<label v-for="option in options" :key="option.value"
					class="px-4 py-2 w-24 border rounded-md cursor-pointer flex justify-center items-center transition-colors"
					:class="selected === option.value ? 'bg-[#3FB8AF] text-white border-[#3FB8AF]' : 'bg-white text-black hover:bg-gray-300'">
					<input type="radio" v-model="selected" :value="option.value" class="hidden" />
					{{ option.label }}
				</label>
			</div>
			<div
				class="bg-white min-w-60 border shadow px-4 h-20 border-gray-200 rounded-md p-2 items-left justify-center flex flex-col">
				<label class="font-bold">기간 단위</label>
				<select class="w-full" v-model="selectedPeriod">
					<option value="daily">일간</option>
					<option value="monthly">월간</option>
				</select>
			</div>
			<div>

			</div>
		</div>
		<div class="bg-white shadow border border-gray-200 rounded-lg p-4 mb-6">
			<h2 class="text-gray-500 font-bold mb-4">기간별 수익 비교</h2>

			<BarChart :chartData="chartData" :options="chartOptions" />
		</div>
		<div class="bg-white shadow border border-gray-200 rounded-lg p-4 mb-6">
			<h2 class="text-gray-500 font-bold mb-4">지역별 수익 비교</h2>
			<PieChart :chartData="locationData" :options="chartOptions" />
		</div>
	</main>
</template>

<style scoped></style>
