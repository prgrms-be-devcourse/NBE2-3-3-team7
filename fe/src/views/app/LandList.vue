<script setup>
import { onMounted, ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useLandFilterStore } from '@/store/land.filter';
import { initFlatpickr, initPriceSlider, initAreaSlider } from '@/utils/init.plugin';
import { landList } from '@/services/land/land.list';
import BasePaging from '@/components/common/BasePaging.vue';
import LandCard from '@/components/app/item/LandCard.vue';
import NoItem from '@/components/common/NoItem.vue';

const router = useRouter();
const route = useRoute();
const filterStore = useLandFilterStore();

const land = ref({ content: [], page: { totalPages: 0, number: 0 } });


onMounted(() => {
	initFlatpickr();

	filterStore.setLocation(route.query.location || "");
	filterStore.setSort(route.query.sort || "");
	filterStore.setPeriod([route.query.start || "", route.query.end || ""]);
	filterStore.minPrice = route.query.minPrice ? Number(route.query.minPrice) : filterStore.minPrice;
	filterStore.maxPrice = route.query.maxPrice ? Number(route.query.maxPrice) : filterStore.maxPrice;
	filterStore.minArea = route.query.minArea ? Number(route.query.minArea) : filterStore.minArea;
	filterStore.maxArea = route.query.maxArea ? Number(route.query.maxArea) : filterStore.maxArea;
	filterStore.setSort(route.query.sort || "");

	initPriceSlider(filterStore);
	initAreaSlider(filterStore);
	fetchLandList();
});

const findLand = () => {
	const params = {
		minArea: filterStore.minArea ?? undefined,
		maxArea: filterStore.maxArea ?? undefined,
		location: filterStore.location || undefined,
		minPrice: filterStore.minPrice ?? undefined,
		maxPrice: filterStore.maxPrice ?? undefined,
		start: filterStore.start || undefined,
		end: filterStore.end || undefined,
		sort: filterStore.sort || undefined,
	};

	// eslint-disable-next-line no-unused-vars
	const query = Object.fromEntries(Object.entries(params).filter(([_, v]) => v !== undefined));

	router.push({ path: '/land', query });
};

watch(
	() => route.query,
	() => {
		fetchLandList();
	},
	{ deep: true }
);

const fetchLandList = async () => {
	try {
		const result = await landList(route.query);
		land.value = result;
	} catch (err) {
		console.error(err);
	}
};
</script>

<template>
	<main class="flex-grow flex flex-col items-center">
		<div class="flex  w-full mt-2 justify-center px-4">
			<div class="max-w-7xl w-full">
				<span class="font-bold text-xl ">임대지 목록</span>
			</div>
		</div>
		<section class="flex w-full px-4 mx-2 md:sticky md:top-0 pt-2 md:z-10 bg-white">
			<div
				class="flex w-full max-w-7xl space-x-0 md:space-x-2 border-t-2 border-b-2 border-[#3FB8AF] mx-auto py-2 justify-between items-center">
				<div class="flex flex-col md:flex-row space-y-2 space-x-0 md:space-y-0 md:space-x-2">
					<div class="flex space-y-2 space-x-0 xl:space-y-0 xl:space-x-2 flex-col xl:flex-row">
						<div class="bg-white px-4 py-2 min-w-60 flex-shrink-0 rounded-md border border-gray-300">
							<div class="mb-2 flex justify-between space-x-2">
								<h3 class="font-bold">면적</h3>
								<label id="area-range" class="text-center"></label>
							</div>
							<div id="area-slider" class="w-full"></div>
						</div>
						<div
							class="bg-white px-4 py-2 flex-col flex-shrink-0 border flex space-x-2 border-gray-300 rounded-md">
							<label class="font-bold">지역</label>
							<select v-model="filterStore.location" class="w-full px-1">
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

					<div class="flex space-y-2 space-x-0 xl:space-y-0 xl:space-x-2 flex-col xl:flex-row">
						<div class="bg-white px-4 py-2 rounded-md border border-gray-300 min-w-60">
							<div class="mb-2 flex justify-between">
								<h3 class="font-bold">금액</h3>
								<label id="price-range" class="text-center"></label>
							</div>
							<div id="price-slider" class="w-full"></div>
						</div>
						<div class="bg-white min-w-60 border px-4 border-gray-300 rounded-md p-2">
							<label for="date-range" class="font-bold">임대 기간</label>
							<input type="text" id="date-range" v-model="filterStore.period" placeholder="기간을 선택하세요."
								class="w-full">
						</div>
					</div>
				</div>
				<div class="flex flex-shrink-0 space-x-0 xl:space-x-2 flex-col xl:flex-row h-full justify-between">
					<div class="bg-white px-4 py-2 flex-col border flex space-x-2 border-gray-300 rounded-md">
						<label class="font-bold">정렬</label>
						<select v-model="filterStore.sort" class="w-full px-1">
							<option value="">정렬</option>
							<option value="registered_desc">최신순</option>
							<option value="registered_asc">등록순</option>
							<option value="price_desc">가격-내림차</option>
							<option value="price_asc">가격-오름차</option>
							<option value="area_desc">면적-내림차</option>
							<option value="area_asc">면적-오름차</option>
						</select>
					</div>
					<button @click="findLand"
						class="py-2 bg-[#3FB8AF] text-white font-bold flex-grow-0 px-4 rounded-md hover:bg-[#2c817c] transition-colors duration-150">
						조회
					</button>
				</div>
			</div>
		</section>

		<section class="w-full px-4">
			<div class="max-w-7xl mx-auto my-6">
				<div v-if="land.content.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
					<LandCard v-for="(item, index) in land.content" :key="index" :item="item.rentalLand"
						:thumbnail="item.thumbnail" />
				</div>
				<NoItem v-else message="조회된 임대지가 없습니다." />
			</div>
		</section>
		<section class="w-full px-4" aria-label="pagination">
			<div
				class="flex items-center flex-col space-y-2 justify-center border-t border-gray-200 bg-white px-4 py-3">
				<BasePaging v-if="land.page.totalPages > 0" :totalPages="land.page.totalPages" :currentPage="land.page.number" url="/land"/>
			</div>
		</section>
	</main>
</template>

<style scoped></style>
