<script setup>
import { RouterLink, useRoute, useRouter } from 'vue-router';
import KakaoMap from '@/components/common/KakaoMap.vue';
import { initFlatpickr } from '@/utils/init.plugin';
import { imageSlider } from '@/utils/global.util';
import { landView } from '@/services/land/land.view';
import { onMounted, ref, computed } from 'vue';
import { getPeriod } from '@/utils/global.util';

const route = useRoute();
const router = useRouter();
const landId = route.params.id;

const dateRange = ref('');
const data = ref({data: {images: [], rentalLand: {}, thumbnail: ""}, period: []});

onMounted(async () => {
	await fetchLand();
	const range = data.value.period.map(item => {
		return { from: item.start, to: item.end };
	});
	initFlatpickr(range);
	imageSlider();
});

const reserve = () => {
	const period = dateRange.value.split(' ~ ');

	if (!period[0] || !period[1]) {
		alert('올바른 기간을 선택해주세요.');
		return;
	}

	const query = {
		land: landId,
		start: period[0],
		end: period[1],
	};

	router.push({ path: '/payment', query });
}

const fetchLand = async () => {
	try {
		const result = await landView(route.params.id); // route.query를 그대로 전달
		data.value = result;
		if(data.value.data.rentalLand === undefined) {
			router.push({ path: '/land' });
		}
	} catch (err) {
		console.error('API 요청 오류:', err);
	};
}

const land = computed(() => data.value.data.rentalLand ?? {});
const images = computed(() => data.value.data.images ?? []);

const totalPrice = computed(() => {
	if (!dateRange.value) return 0;

	const period = dateRange.value.split(' ~ ');

	if (!period[0] || !period[1]) return 0;

	const days = getPeriod(period[0], period[1]);

	return days * land.value.price;
});
</script>

<template>
	<main class="flex-grow flex flex-col items-center">
		<div class="flex mt-2 max-w-7xl w-full justify-center px-4">
			<div class="max-w-7xl w-full flex items-center flex-shrink-0">
				<router-link to="/land" class="font-bold p-2 space-x-2 flex items-center">
					<i class="fas fa-angles-left"></i>
					<span id="title" class="font-bold text-xl">{{ land.title }}</span>
				</router-link>
				
			</div>
		</div>
		<section class="flex w-full justify-center">
			<div class="flex flex-col lg:flex-row px-4 max-w-7xl w-full space-y-4 lg:space-y-0 lg:space-x-4">
				<div class="p-4 w-full border rounded-lg border-gray-300">
					<div class="flex-1 space-y-4">
						<div
							class="relative border overflow-hidden border-gray-300 rounded-md h-64 md:h-[24rem] lg:h-[36rem] bg-gray-300">
							<div id="image-container" class="flex w-full transition-transform h-full duration-1000"
								style="transform: translateX(0%);"> <!-- TODO : 추후 확인 필요 -->
								<div v-for="(image, index) in images" :key="index"  class="w-full h-full flex-shrink-0"> 
									<img :src="image" class="mx-auto h-full w-full object-cover" alt="land.title">
								</div>
							</div>
							<div class="absolute inset-0 w-full flex items-center justify-between px-4">
								<button id="prev-img-btn"
									class="text-2xl font-bold py-2 px-4 rounded-full hover:bg-gray-300 opacity-50 transition-colors"><i class="fas fa-angle-left"></i></button>
								<button id="next-img-btn"
									class="text-2xl font-bold py-2 px-4 rounded-full hover:bg-gray-300 opacity-50 transition-colors"><i class="fas fa-angle-right"></i></button>
							</div>
							<div class="absolute bottom-2 right-4 text-sm font-bold"><span id="slide-img-num"></span> /
								<span id="slide-img-total"></span>
							</div>
						</div>
						<div
							class="flex lg:space-x-4 lg:space-y-0 space-x-0 space-y-4 lg:flex-row flex-col justify-between">
							<!-- 주요 정보 -->
							<div class="space-y-4 text-sm w-full">
								<div class="space-y-2">
									<strong>주요 인프라</strong>
									<div id="infra-box" class="space-x-2">
										<span v-for="infra in land.infra?.split(',')" :key="infra"
											class="inline-flex items-center rounded-xl bg-white shadow-md px-4 py-2 text-xs text-black font-bold ring-2 ring-inset ring-[#3FB8AF] cursor-default">
											{{ infra }}
										</span>
									</div>
								</div>
								<div class="space-y-2">
									<strong>주변 연령대</strong>
									<p id="age-group">{{ land.ageGroup }}</p>
								</div>
								<div class="space-y-2">
									<strong>임대지 면적</strong>
									<p id="place-area">{{ land.area }}평</p>
								</div>
								<div class="space-y-2">
									<strong>임대지 설명</strong>
									<p id="place-description">
										{{ land.description }}
									</p>
								</div>
								<div class="space-y-2">
									<strong>임대지 주소</strong>
									<p id="place-full-address">{{ `[${land.zipcode}] ${land.address}, ${land.addrDetail}` }}</p>
									<div class="border border-gray-300 rounded-md">
										<div class="h-60 w-full bg-gray-200 items-center flex justify-center">
											<KakaoMap :data="land.address" />
										</div>
									</div>
								</div>
							</div>

							<div
								class="w-full flex-shrink-0 lg:w-72 border max-h-fit border-gray-300 rounded-md p-4 shadow-md sticky top-4">
								<div class="text-center font-bold mb-2 pb-2 border-b">임대</div>
								<div class="text-sm space-y-2 flex flex-col">
									<div class="flex flex-col">
										<span class="font-bold text-sm">가격</span>
										<span id="place-price" class="font-bold text-lg">{{ land.price?.toLocaleString() }}원 / 일</span>
									</div>
									<div class="flex flex-col">
										<label for="date-range" class="font-bold tex-sm">임대 기간</label>
										<div class="p-2 my-2 border border-gray-300 rounded-md">
											<input type="text" id="date-range" v-model="dateRange"
												placeholder="기간을 선택하세요." class="w-full">
										</div>
									</div>
									<div class="flex flex-col">
										<span class="font-bold text-sm">총 금액</span>
										<span id="total-price" class="font-bold text-lg">{{ totalPrice.toLocaleString() }}원</span>
									</div>
								</div>
								<button @click="reserve"
									class="block text-center w-full bg-[#3FB8AF] text-white py-2 rounded-md mt-2 hover:bg-[#2c817c] transition-colors">
									예약하기
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</section>
	</main>
</template>

<style scoped></style>
