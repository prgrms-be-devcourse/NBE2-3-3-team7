<script setup>
import { RouterLink } from 'vue-router';

defineProps({
	item: {
		type: Object,
		required: true,
	},
	thumbnail: {
		type: String,
		required: true,
	},
});
</script>

<template>
	<router-link :to="`/popup/${item.id}`"
		class="group drop-shadow-lg relative p-4 border m-2 rounded-lg border-gray-300">
		<div class="absolute w-full h-full bg-gray-300 opacity-0 group-hover:opacity-50 transition left-0 top-0"></div>
		<div class="mt-2 block relative overflow-hidden rounded-lg border border-gray-400">
			<img class="w-full  h-44 object-contain bg-gray-100" :src="thumbnail" :alt="item.title">
			<div class="absolute bottom-1 end-1 opacity-0 group-hover:opacity-100 transition">
				<div
					class="flex items-center z-10 gap-x-1 py-1 px-2 bg-white border border-gray-200 text-gray-800 rounded-lg">
					<svg class="shrink-0 size-3" width="24" height="24" viewBox="0 0 24 24" fill="none"
						stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
						<circle cx="11" cy="11" r="8" />
						<path d="m21 21-4.3-4.3" />
					</svg>
					<span class="text-xs">상세보기</span>
				</div>
			</div>
		</div>
		<div class="space-y-2 mt-4">
			<h3 class="text-lg font-semibold text-gray-900">{{ item.title }}</h3>
			<h3 class="font-semibold text-gray-900">{{ `${item.startDate} ~ ${item.endDate}` }}</h3>
			<div class="flex justify-between">
				<span>{{ item.address }}</span>
				<span>{{ item.type }}</span>
			</div>
			<div class="flex justify-between">
				<!-- <h3 class="text-lg font-bold text-gray-900 hover:text-[#3FB8AF] transition-colors"><a
						href="/mypage/rental/view/${item.id}">${item.name}</a></h3>
				<label class="switch-label">
					<span class="font-bold text-xs">등록 상태</span>
					<input role="switch" type="checkbox" name="status-switch" data-seq="${item.id}"
						class="transition-colors" checked />
				</label> -->
			</div>
		</div>
	</router-link>
</template>

<style scoped></style>
