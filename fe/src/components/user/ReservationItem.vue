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
	<div class="drop-shadow-lg relative p-4 border m-2 rounded-lg border-gray-300 cursor-default bg-white">
		<div class="flex flex-col justify-between">
			<div class="flex justify-between items-center h-9">
				<span class="text-sm block" :class="{
					'text-gray-700': item.status === '결제 완료',
					'text-red-500': item.status === '환불 완료',
					'text-[#3FB8AF]': item.status === '임대 완료'
				}">{{ item.status }}</span>
			</div>
			<div class="flex justify-between items-center h-9">
				<span class="text-gray-700 text-md block">고객명 : {{ item.customerName }}</span>
				<span class="text-gray-700 text-sm block">{{ item.price?.toLocaleString() }}원 / 일</span>
			</div>
			<div class="flex justify-between items-center h-9">
				<span class="text-gray-700 text-sm block">임대 기간 : {{ getKorDateRange(item.start, item.end) }}</span>
				<span class="text-gray-700 font-bold block">총 금액 {{ item.amount?.toLocaleString() }}원</span>
			</div>
		</div>
	</div>
</template>

<style scoped>
ul {
	list-style: none;
	margin: 0;
	padding: 0;
}
</style>
