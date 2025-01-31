<script setup>
import { onMounted, ref } from 'vue';
import LandCard from '@/components/app/item/LandCard.vue';
import PopupCard from '@/components/app/item/PopupCard.vue';
import LandFilter from '@/components/app/home/LandFilter.vue';
import PopupFilter from '@/components/app/home/PopupFilter.vue';
import { homeItem } from '@/services/home/home.api';
import { useRoute } from 'vue-router';

const route = useRoute();

const data = ref({});

const selected = ref('land');

onMounted(() => {
	slideImage();
	fetchHomeItem();

});

const fetchHomeItem = async () => {
	try {
		const result = await homeItem(route.query); // route.query를 그대로 전달
		data.value = result;
		console.log(result);
	} catch (err) {
		console.error('API 요청 오류:', err);
	}
};

function slideImage() {
	const slides = document.getElementById('slide-container');
	const slideItems = document.querySelectorAll('#slide-container > div');
	const indicators = document.querySelectorAll('.indicator');

	let interval_3;

	let index = 0;

	const totalSlides = slideItems.length;

	const prevButton = document.getElementById('left-slide-arr');
	const nextButton = document.getElementById('right-slide-arr');

	prevButton.addEventListener('click', () => {
		index = (index - 1 + totalSlides) % totalSlides;
		moveSlide(slides, index);
		resetInterval();
	});

	nextButton.addEventListener('click', () => {
		index = (index + 1) % totalSlides;
		moveSlide(slides, index);
		resetInterval();
	});

	indicators.forEach((indicator, i) => {
		indicator.addEventListener('click', () => {
			index = i;
			moveSlide(slides, index);
			resetInterval();
		});
	});

	resetInterval();

	function resetInterval() {
		if (interval_3) clearInterval(interval_3);

		interval_3 = setInterval(() => {
			index = (index + 1) % totalSlides;
			moveSlide(slides, index);
		}, 5000);
	}

	function moveSlide(slides, index) {
		slides.style.transform = `translateX(-${index * 100}%)`;
	}
}


</script>

<template>
	<main class="flex-grow flex flex-col items-center">
		<article class="relative w-full min-h-96 max-h-[32rem] flex overflow-hidden bg-white">
			<!-- 슬라이드 배너 컨테이너 -->
			<div id="slide-container" class="flex w-full transition-transform duration-1000"
				style="transform: translateX(0%);">
				<!-- 슬라이드 항목 1 -->
				<div class="w-full h-full flex-shrink-0">
					<img src="../../assets/images/slides/slide1.png" class="mx-auto h-full w-full object-contain" alt="">
				</div>
				<!-- 슬라이드 항목 2 -->
				<div class="w-full h-full flex-shrink-0">
					<img src="../../assets/images/slides/slide2.png" class="mx-auto h-full w-full object-contain" alt="">
				</div>
				<!-- 슬라이드 항목 3 -->
				<div class="w-full h-full flex-shrink-0">
					<img src="../../assets/images/slides/slide3.png" class="mx-auto h-full w-full object-contain" alt="">
				</div>
			</div>
			<div class="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex space-x-4 items-center">
				<button id="left-slide-arr"
					class="text-2xl text-white px-3 rounded-full hover:bg-gray-300 opacity-50 flex items-center justify-center">
					&#10094;
				</button>
				<button class="indicator hover:bg-blue-300 w-5 h-5 bg-gray-400 opacity-50 rounded-full"></button>
				<button class="indicator hover:bg-blue-300 w-5 h-5 bg-gray-400 opacity-50 rounded-full"></button>
				<button class="indicator hover:bg-blue-300 w-5 h-5 bg-gray-400 opacity-50 rounded-full"></button>
				<button id="right-slide-arr"
					class="text-2xl text-white px-3 rounded-full hover:bg-gray-300 opacity-50 flex items-center justify-center">
					&#10095;
				</button>
			</div>
		</article>
		<section class="flex w-full justify-center mt-10">
			<div class="flex max-w-7xl w-full px-4 flex-col">
				<div class="flex">
					<input type="radio" name="filter" id="filter-land" checked class="hidden" v-model="selected"
						value="land" />
					<label for="filter-land" :class="{
						'relative transform text-white transition-all duration-150 p-2 rounded-t-md font-bold bg-[#3FB8AF]': selected === 'land',
						'relative transform text-black translate-y-2 transition-all duration-150 p-2 rounded-t-md font-bold bg-[#d8d7d7]': selected !== 'land'
					}">
						장소 찾아요🙋
					</label>
					<input type="radio" name="filter" id="filter-popup" class="hidden" v-model="selected"
						value="popup" />
					<label for="filter-popup" :class="{
						'relative transform text-white transition-all duration-150 p-2 rounded-t-md font-bold bg-[#3FB8AF]': selected === 'popup',
						'relative transform text-black translate-y-2 transition-all duration-150 p-2 rounded-t-md font-bold bg-[#d8d7d7]': selected !== 'popup'
					}">
						팝업 찾아요🙋
					</label>
				</div>
				<div
					class="z-10 bg-white max-w-7xl border-t-2 border-b-2 border-[#3FB8AF] w-full p-4 flex items-center justify-center min-h-28">
					<LandFilter v-if="selected === 'land'" />
					<PopupFilter v-else />
				</div>
			</div>
		</section>

		<section class="mt-10 w-full px-4">
			<div class="border-2 max-w-7xl  mx-auto rounded-2xl ">
				<h3 class="pl-8 mt-4 text-2xl">신규 임대지</h3>
				<div class="m-4"><!-- Snap Point -->
					<div class="w-full flex gap-6 snap-x list_scrollbar overflow-x-auto pb-6">
						<LandCard v-for="(item, index) in data.land" :key="index" :item="item.rentalLand"
						:thumbnail="item.thumbnail" class="w-1/3 flex-shrink-0 snap-center"/>
					</div>
				</div>
			</div>
		</section>
		<section class="mt-10 w-full px-4">
			<div class="border-2 max-w-7xl mx-auto rounded-2xl ">
				<h3 class="pl-8 mt-4 text-2xl">신규 팝업 스토어</h3>
				<div class="m-4"><!-- Snap Point -->
					<div v-if="data.popup" class="w-full flex gap-6 snap-x list_scrollbar overflow-x-auto pb-6">
						<PopupCard v-for="(item, index) in data.popup" :key="index" :item="item.popup"
						:thumbnail="item.thumbnail" class="w-1/3 flex-shrink-0 snap-center"/>
					</div>
				</div>
			</div>
		</section>
	</main>
</template>

<style scoped></style>
