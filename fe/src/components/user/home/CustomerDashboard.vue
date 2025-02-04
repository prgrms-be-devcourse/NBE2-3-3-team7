<script setup>
import { RouterLink } from 'vue-router';
import NoItem from '@/components/common/NoItem.vue';
import PopupCard from './PopupCard.vue';
import ReceiptItem from '@/components/user/ReceiptItem.vue';

defineProps({
	popup: {
		type: Array,
		required: true,
	},
	payment: {
		type: Array,
		required: true,
	},
});
</script>

<template>
	<section class="mt-10 w-full flex justify-center">
		<div class="flex flex-col justify-center max-w-4xl w-full space-y-10 items-center">
			<!-- 최근 팝업 스토어 목록 -->
			<article class="flex flex-col rounded-2xl p-4 bg-white border border-gray-300 shadow-md w-full">
				<div class="flex justify-between pb-4">
					<h3 class="text-lg font-bold">최근 팝업 스토어 목록</h3>
					<router-link to="/user/popup"
						class="text-lg text-black font-bold hover:text-[#3FB8AF] transition-colors">
						전체 보기
					</router-link>
				</div>
				<div class="border-y border-gray-300">
					<div v-if="popup.length" class="auto grid grid-cols-2 py-4 gap-2 snap-y height_scrollbar overflow-y-auto h-96">
						<PopupCard v-for="(item, index) in popup" :key="index" :item="item.popup"
							:thumbnail="item.thumbnail" class="col-span-1 snap-center" />
					</div>
					<NoItem v-else message="등록된 팝업 스토어가 없습니다." />
				</div>
			</article>

			<article class="flex flex-col rounded-2xl p-4 bg-white border border-gray-300 shadow-md w-full">
				<div class="flex justify-between pb-4">
					<h3 class="text-lg font-bold">최근 결제 내역</h3>
					<router-link to="/user/payment"
						class="text-lg text-black font-bold hover:text-[#3FB8AF] transition-colors">
						전체 보기
					</router-link>
				</div>
				<div class="border-y border-gray-300">
					<div v-if="payment.length" class="auto grid grid-cols-2 py-4 gap-2 snap-y height_scrollbar overflow-y-auto h-56">
						<ReceiptItem v-for="(item, index) in payment" :key="index" :item="item" class="col-span-1 snap-center"/>
					</div>
					<NoItem v-else message="조회된 결제 내역이 없습니다." />
				</div>
			</article>
		</div>
	</section>
</template>

<style scoped></style>