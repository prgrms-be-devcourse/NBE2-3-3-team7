<script setup>
import { onMounted, ref, watch } from 'vue';
import RouterBack from '@/components/common/RouterBack.vue';
import NoItem from '@/components/common/NoItem.vue';
import { useRoute } from 'vue-router';
import BasePaging from '@/components/common/BasePaging.vue';
import { receiptByUser } from '@/services/user/user.payment';
import PaymentCard from "@/components/user/home/PaymentCard.vue";

const route = useRoute();

const payment = ref({ content: [], page: { totalPages: 0, number: 0 } });

onMounted(async () => {
	await fetchReceiptInfo()
})

watch(
	() => route.query,
	() => {
		fetchReceiptInfo();
	},
	{ deep: true }
);

const fetchReceiptInfo = async () => {
	try {
		const result = await receiptByUser(route.query); // route.query를 그대로 전달
		payment.value = result;
		console.log(result)
	} catch (err) {
		console.error('API 요청 오류:', err);
	}
};
</script>

<template>
	<main class="flex-grow flex flex-col items-center relative">
		<RouterBack link="/user" text="임대지 결제 내역 목록" />
		<section class="mt-4 w-full px-4">
			<div class="max-w-4xl mx-auto my-6">
				<div v-if="payment.content.length > 0" class="grid grid-cols-1 gap-5">
					<PaymentCard v-for="(item, index) in payment.content" :key="index" :item="item" />
				</div>
				<NoItem v-else message="팝업 스토어가 존재하지 않습니다." class="col-span-2" />
			</div>
		</section>
		<section class="flex py-4 max-w-5xl w-full border-b border-gray-300 justify-center px-4"
			aria-label="pagination">
			<div v-if="payment.page.totalPages > 0"
				class="flex items-center flex-col space-y-2 justify-center bg-white px-4 py-3">
				<BasePaging :totalPages="payment.page.totalPages" :currentPage="payment.page.number" url="/user/payment" />
			</div>
		</section>
	</main>
</template>