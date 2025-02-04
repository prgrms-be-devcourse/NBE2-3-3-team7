<script setup>
import { computed } from "vue";
import { RouterLink } from "vue-router";
import ToggleSwitch from "@/components/common/ToggleSwitch.vue";

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

const emit = defineEmits(["update-status"]);

// ✅ `item.status`를 Boolean 값으로 변환하여 `ToggleSwitch`에 전달
const itemStatus = computed({
	get: () => props.item.status === "ACTIVE",
	set: (newValue) => {
		emit("update-status", { id: props.item.id, status: newValue ? "ACTIVE" : "INACTIVE" });
	}
});
</script>

<template>
	<div class="group drop-shadow-lg relative p-4 border m-2 rounded-lg border-gray-300 cursor-default">
		<router-link :to="`/user/land/${item.id}`"
			class="mt-2 block relative overflow-hidden rounded-lg border border-gray-400 cursor-pointer">
			<img class="w-full h-44 object-contain bg-gray-100" :src="thumbnail" :alt="item.title">
		</router-link>

		<div class="space-y-2 mt-4">
			<div class="flex justify-between">
				<h3 class="text-lg font-semibold text-gray-900">{{ item.title }}</h3>
				<ToggleSwitch v-model:status="itemStatus" :id="item.id" type="land" />
			</div>
			<div class="flex justify-between">
				<router-link :to="`/user/land/${item.id}/reservation`" class="hover:underline text-[#3FB8AF] hover:text-[#2c817c] transition-colors">
					예약 현황 보기
				</router-link>
				<h3 class="font-semibold text-gray-900">{{ `${item.price.toLocaleString()}원 / 일` }}</h3>
			</div>
			<div class="flex justify-between">
				<span>{{ item.address }}</span>
				<span>{{ `${item.area}평` }}</span>
			</div>
		</div>
	</div>
</template>