<script setup>
import { RouterLink } from 'vue-router';
import { getKorDateRange } from '@/utils/global.util'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue';
import { computed } from "vue";

const props = defineProps({
	item: {
		type: Object,
		required: true,
	},
	thumbnail: {
		type: String,
		required: true,
	},
});

const emit = defineEmits(['update-status']);

const itemStatus = computed({
	get: () => props.item.status === "ACTIVE",
	set: (newValue) => {
		emit("update-status", { id: props.item.id, status: newValue ? "ACTIVE" : "INACTIVE" });
	}
});
</script>

<template>
	<div class="drop-shadow-lg relative p-4 border m-2 rounded-lg border-gray-300">
		<router-link :to="`/user/popup/${item.id}`"
			class="group mt-2 block relative overflow-hidden rounded-lg border border-gray-400 cursor-pointer">
			<img class="w-full h-44 object-contain bg-gray-100" :src="thumbnail" :alt="item.title">
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
		</router-link>
		<div class="space-y-2 mt-4">
			<div class="flex justify-between">
				<h3 class="text-lg font-semibold text-gray-900">{{ item.title }}</h3>
				<ToggleSwitch v-model:status="itemStatus" :id="item.id" type="popup" />
			</div>
			<h3 class="font-semibold text-gray-900">{{ getKorDateRange(item.startDate, item.endDate) }}</h3>
			<div class="flex justify-between">
				<span>{{ item.address }}</span>
				<span>{{ item.type }}</span>
			</div>
		</div>
	</div>
</template>

<style scoped></style>