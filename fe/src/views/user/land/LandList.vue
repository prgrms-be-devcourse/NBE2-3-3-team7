<script setup>
import { onMounted, ref, watch } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import RouterBack from '@/components/common/RouterBack.vue';
import LandCard from '@/components/user/home/LandCard.vue';
import BasePaging from '@/components/common/BasePaging.vue';
import NoItem from '@/components/common/NoItem.vue';
import { landList } from '@/services/user/user.land';

const route = useRoute();

const land = ref({ content: [], page: { totalPages: 0, number: 0 } });

onMounted(async () => {
	await fetchPopupList()
})

watch(
	() => route.query,
	() => {
		fetchPopupList();
	},
	{ deep: true }
);

const fetchPopupList = async () => {
	try {
		const result = await landList(route.query); // route.query를 그대로 전달
		land.value = result;
	} catch (err) {
		console.error('API 요청 오류:', err);
	}
};

</script>

<template>
	<main class="flex-grow flex flex-col items-center">
		<RouterBack link="/user" text="임대지 목록">
			<router-link to="/user/land/add"
				class="text-white bg-[#3FB8AF] hover:bg-[#2c817c] py-2 px-4 rounded-md transition-colors font-bold cursor-pointer">
				임대지 추가
			</router-link>
		</RouterBack>
		<section class="mt-4 w-full px-4">
			<div class="max-w-4xl mx-auto my-6">
				<div v-if="land.content.length > 0" class="grid grid-cols-1 sm:grid-cols-2 gap-5">
					<LandCard v-for="(item, index) in land.content" :key="index" :item="item.rentalLand"
						:thumbnail="item.thumbnail" />
				</div>
				<NoItem v-else message="임대지가 존재하지 않습니다." class="col-span-2" />
			</div>
		</section>
		<section class="flex py-4 max-w-5xl w-full border-b border-gray-300 justify-center px-4"
			aria-label="pagination">
			<div v-if="land.page.totalPages > 0"
				class="flex items-center flex-col space-y-2 justify-center bg-white px-4 py-3">
				<BasePaging :totalPages="land.page.totalPages" :currentPage="land.page.number" url="/user/land" />
			</div>
		</section>
	</main>
</template>
