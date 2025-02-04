<script setup>
import { RouterLink } from 'vue-router';
import NoItem from '@/components/common/NoItem.vue';
import LandAnalytics from './LandAnalytics.vue';
import LandCard from './LandCard.vue';

defineProps({
	land: {
		type: Array,
		required: true,
	},
	analytics: {
		type: Array,
		required: true,
	},
});

</script>

<template>
	<section class="mt-10 w-full flex justify-center">
		<div class="flex flex-col justify-center max-w-4xl w-full space-y-10 items-center">
			<article class="flex flex-col rounded-2xl p-4 bg-white border border-gray-300 shadow-md w-full">
				<div class="flex justify-between pb-4">
					<h3 class="text-lg font-bold">
						최근 임대지 목록
					</h3>
					<router-link to="/user/land"
						class="text-lg text-black font-bold hover:text-[#3FB8AF] transition-colors">
						전체 보기
					</router-link>
				</div>
				<div class="border-y border-gray-300">
					<div v-if="land.length" class="auto grid grid-cols-2 py-4 gap-2 snap-y height_scrollbar overflow-y-auto h-96">
						<LandCard v-for="(item, index) in land" :key="index" :item="item.rentalLand"
							:thumbnail="item.thumbnail" class="col-span-1 snap-center" />
					</div>
					<NoItem v-else class="col-span-2" message="등록된 임대지가 없습니다." />
				</div>
			</article>
			<article class="flex flex-col rounded-2xl p-4 bg-white border border-gray-300 shadow-md w-full">
				<div class="flex justify-between pb-4">
					<h3 class="text-lg font-bold">
						최근 수익 현황
					</h3>
					<router-link to="/user/stats"
						class="text-lg text-black font-bold hover:text-[#3FB8AF] transition-colors">
						전체 보기
					</router-link>
				</div>
				<div class="border-y border-gray-300">
					<div v-if="analytics.length">
						<LandAnalytics />
					</div>
					<NoItem v-else message="조회된 정보가 없습니다." />
				</div>
			</article>
		</div>
	</section>
</template>

<style scoped></style>
