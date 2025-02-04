<script setup>
import { onMounted, } from 'vue';
import { useRouter } from 'vue-router';
import { useLandFilterStore } from '@/store/land.filter';
import { initFlatpickr, initPriceSlider, initAreaSlider } from '@/utils/init.plugin';

const router = useRouter();
const filterStore = useLandFilterStore();

filterStore.resetFilters();

onMounted(() => {
	initFlatpickr();
	filterStore.resetFilters();
	initPriceSlider(filterStore);
	initAreaSlider(filterStore);
});

const findLand = () => {
	const loc = document.getElementById('location');

	filterStore.setPeriod(document.getElementById('date-range').value.split(' ~ ')); // ✅ 기간 설정
	filterStore.setLocation(loc.value || "");

	const params = {
		minArea: filterStore.minArea ?? undefined,
		maxArea: filterStore.maxArea ?? undefined,
		location: filterStore.location || undefined,
		minPrice: filterStore.minPrice ?? undefined,
		maxPrice: filterStore.maxPrice ?? undefined,
		start: filterStore.start || undefined,
		end: filterStore.end || undefined,
	};

	// eslint-disable-next-line no-unused-vars
	const query = Object.fromEntries(Object.entries(params).filter(([_, v]) => v !== undefined));

	router.push({ path: '/land', query });
};

</script>

<template>
	<div id="filter-box" class="flex flex-col min-w-80 lg:flex-row lg:space-y-0 lg:space-x-4 space-y-4">
		<div class="flex space-x-4">
			<div class="bg-white px-4 py-2 rounded-md border border-gray-300 min-w-[12rem]">
				<div class="mb-2 flex justify-between space-x-2">
					<h3 class="font-bold">면적</h3>
					<span id="area-range" class="text-center"></span>
				</div>
				<div id="area-slider" class="w-full"></div>
			</div>
			<div class="bg-white px-4 py-2 flex-shrink-0 border border-gray-300 rounded-md">
				<label for="location" class="font-bold">지역</label>
				<select id="location" class="w-full px-1">
					<option value="">전체</option>
					<option value="서울">서울</option>
					<option value="부산">부산</option>
					<option value="대구">대구</option>
					<option value="인천">인천</option>
					<option value="광주">광주</option>
					<option value="대전">대전</option>
					<option value="울산">울산</option>
					<option value="세종">세종</option>
					<option value="경기">경기</option>
					<option value="충북">충북</option>
					<option value="충남">충남</option>
					<option value="전북">전북</option>
					<option value="전남">전남</option>
					<option value="경북">경북</option>
					<option value="경남">경남</option>
					<option value="강원">강원</option>
					<option value="제주">제주</option>
				</select>
			</div>
		</div>
		<div class="bg-white px-4 py-2 rounded-md border border-gray-300 min-w-72">
			<div class="mb-2 flex justify-between">
				<h3 class="font-bold">금액</h3>
				<span id="price-range" class="text-center"></span>
			</div>
			<div id="price-slider" class="w-full"></div>
		</div>
		<div class="bg-white min-w-60 border px-4 border-gray-300 rounded-md p-2">
			<label for="date-range" class="block font-bold">임대 기간</label>
			<input type="text" id="date-range" v-model="filterStore.period" placeholder="기간을 선택하세요." class="w-full">
		</div>

		<button @click="findLand"
			class="py-2 bg-[#3FB8AF] text-white font-bold flex-shrink-0 flex-grow-0 px-4 rounded-md hover:bg-[#2c817c] transition-colors duration-150">
			조회
		</button>
	</div>
</template>

<style scoped></style>