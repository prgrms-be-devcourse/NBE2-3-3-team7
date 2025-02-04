<script setup>
import { onMounted, ref, watch } from 'vue';
import RouterBack from '@/components/common/RouterBack.vue';
import PopupCard from '@/components/user/home/PopupCard.vue';
import NoItem from '@/components/common/NoItem.vue';
import { useRoute } from 'vue-router';
import BasePaging from '@/components/common/BasePaging.vue';
import { popupList } from '@/services/user/user.popup';

const route = useRoute();

const popup = ref({ content: [], page: { totalPages: 0, number: 0 } });

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
		const result = await popupList(route.query); // route.query를 그대로 전달
		popup.value = result;
	} catch (err) {
		console.error('API 요청 오류:', err);
	}
};

</script>

<template>
	<main class="flex-grow flex flex-col items-center">
		<RouterBack link="/user" text="팝업 스토어 목록">
			<router-link to="/user/popup/add"
				class="text-white bg-[#3FB8AF] hover:bg-[#2c817c] py-2 px-4 rounded-md transition-colors font-bold cursor-pointer">
				팝업 추가
			</router-link>
		</RouterBack>
		<section class="mt-4 w-full px-4">
			<div class="max-w-4xl mx-auto my-6">
				<div v-if="popup.content.length > 0" class="grid grid-cols-1 sm:grid-cols-2 gap-5">
					<PopupCard v-for="(item, index) in popup.content" :key="index" :item="item.popup"
						:thumbnail="item.thumbnail" />
				</div>
				<NoItem v-else message="팝업 스토어가 존재하지 않습니다." class="col-span-2" />
			</div>
		</section>
		<section class="flex py-4 max-w-5xl w-full border-b border-gray-300 justify-center px-4"
			aria-label="pagination">
			<div v-if="popup.page.totalPages > 0"
				class="flex items-center flex-col space-y-2 justify-center bg-white px-4 py-3">
				<BasePaging :totalPages="popup.page.totalPages" :currentPage="popup.page.number" url="/user/popup" />
			</div>
		</section>
	</main>
</template>