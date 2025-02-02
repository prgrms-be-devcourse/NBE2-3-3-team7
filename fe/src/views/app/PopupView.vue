<script setup>
import { RouterLink, useRoute, useRouter } from 'vue-router';
import KakaoMap from '@/components/common/KakaoMap.vue';
import { onMounted, ref, computed } from 'vue';
import { imageSlider, getKorDateRange } from '@/utils/global.util';
import { popupView } from '@/services/popup/popup.view';

const route = useRoute();
const router = useRouter();
const popupId = route.params.id;

const data = ref({images: [], popup: {}, thumbnail: ""});

onMounted(() => {
	fetchPopup();
	imageSlider();
});

const fetchPopup = async () => {
	try {
		const result = await popupView(popupId);
		data.value = result;
		console.log(result);
		if(data.value.popup === undefined) {
			router.push({ path: '/popup' });
		}
	} catch (err) {
		console.error('API 요청 오류:', err);
	};
}

const popup = computed(() => data.value.popup ?? {});
const images = computed(() => data.value.images ?? []);

</script>

<template>
	<main class="flex-grow flex flex-col items-center">
		<div class="flex mt-2 max-w-7xl w-full justify-center px-4">
			<div class="max-w-7xl w-full flex items-center flex-shrink-0">
				<router-link to="/popup" class="font-bold p-2 space-x-2 flex items-center">
					<i class="fas fa-angles-left"></i>
					<span id="title" class="font-bold text-xl">{{ popup.title }}</span>
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
								style="transform: translateX(0%);">
								<div v-for="(image, index) in images" :key="index" class="w-full h-full flex-shrink-0"> <!-- v-for -->
									<img :src="image" class="mx-auto h-full w-full object-cover" :alt="popup.title">
								</div>
							</div>
							<div class="absolute inset-0 w-full flex items-center justify-between px-4">
								<button id="prev-img-btn"
									class="text-2xl font-bold py-2 px-4 rounded-full hover:bg-gray-300 opacity-50 transition-colors">&#10094;</button>
								<button id="next-img-btn"
									class="text-2xl font-bold py-2 px-4 rounded-full hover:bg-gray-300 opacity-50 transition-colors">&#10095;</button>
							</div>
							<div class="absolute bottom-2 right-4 text-sm font-bold"><span id="slide-img-num"></span> /
								<span id="slide-img-total"></span>
							</div>
						</div>

						<div
							class="flex lg:space-x-4 lg:space-y-0 space-x-0 space-y-4 lg:flex-row flex-col justify-between">
							<div class="space-y-4 text-sm w-full">
								<div class="space-y-2">
									<strong>주요 인프라</strong>
									<div id="infra-box" class="space-x-2">
										<span v-for="(infra, index) in popup.infra?.split(',')" :key="index"
											class="inline-flex items-center rounded-xl bg-white shadow-md px-4 py-2 text-xs text-black font-bold ring-2 ring-inset ring-[#3FB8AF] cursor-default">
											{{ infra }}
										</span>
									</div>
								</div>
								<div class="space-y-2">
									<strong>팝업 유형</strong>
									<p id="popup-type">{{ popup.type }}</p>
								</div>
								<div class="space-y-2">
									<strong>팝업 기간</strong>
									<p id="rental-date">{{ getKorDateRange(popup.startDate, popup.endDate) }}</p>
								</div>
								<div class="space-y-2">
									<strong>팝업 설명</strong>
									<p id="popup-description">
										{{ popup.description }}
									</p>
								</div>
								<div class="space-y-2">
									<strong>팝업 주소</strong>
									<p id="place-full-address">{{ `[${popup.zipcode}] ${popup.address}, ${popup.addrDetail}` }}</p>
									<div class="border border-gray-300 rounded-md">
										<div class="h-80 w-full bg-gray-200 items-center flex justify-center">
											<KakaoMap :addr="popup.address" />
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</section>
	</main>
</template>

<style scoped></style>
