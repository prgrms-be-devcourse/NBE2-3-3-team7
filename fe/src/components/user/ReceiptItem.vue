<script setup>
import { getKorDateRange } from '@/utils/global.util';

defineProps({
	item: {
		type: Object,
		required: true,
	}
});
</script>

<template>
	<div class="rounded-lg flex flex-col justify-center items-center p-4 m-2 space-y-2 shadow-xl bg-white border border-gray-300">
		<div class="flex text-sm w-full justify-between border-b pb-2 border-gray-300">
			<span class="text-sm h-6 font-bold flex items-center" :class="{
				'text-gray-700': item.status === '결제 완료',
				'text-red-500': item.status === '환불 완료',
				'text-[#3FB8AF]': item.status === '임대 완료'
			}">{{ item.status }}</span>
		</div>
		<span class="font-bold text-lg">{{ item.landTitle }}</span>
		<span class="font-bold text-base">{{ getKorDateRange(item.start, item.end) }}</span>
		<div class="flex font-bold w-full justify-between">
			<span>{{ item.price.toLocaleString() }}원 / 일</span>
			<span>총합 {{ item.amount.toLocaleString() }}원</span>
		</div>
	</div>
</template>

<style scoped></style>