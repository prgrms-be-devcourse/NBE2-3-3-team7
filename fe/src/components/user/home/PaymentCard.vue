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
		<div class="flex justify-between items-center">

			<div class="flex flex-col">
				<span class="text-sm mb-2 h-6 flex items-center" :class="{
					'text-gray-700': item.status === '결제 완료',
					'text-red-500': item.status === '환불 완료',
					'text-[#3FB8AF]': item.status === '임대 완료'
				}">
					{{ item.status }}
				</span>
				<span class="text-gray-700 text-lg font-semibold mb-2 h-6 flex items-center">{{ item.landTitle }}</span>
				<span class="text-gray-700 text-sm h-6 flex items-center">{{ getKorDateRange(item.start, item.end) }}</span>
			</div>
			
			<div class="text-right">
				<button class="text-red-500 hover:text-red-700 text-sm mb-2 font-medium h-6 flex items-center w-full justify-end">환불하기</button>
				<span class="text-lg font-bold text-gray-900 mb-2 h-6 flex items-center w-full justify-end">총 금액 {{ item.amount.toLocaleString() }}원</span>
				<span class="text-sm text-gray-700 h-6 flex items-center w-full justify-end">{{ item.price.toLocaleString() }}원 / 일</span>
			</div>
		</div>
	</div>
</template>

<style scoped></style>