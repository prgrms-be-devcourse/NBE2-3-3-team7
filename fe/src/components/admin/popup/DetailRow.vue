<script setup>
defineProps({
	row: {
		type: Object,
		required: true,
	},
});
</script>

<template>
	<tr class="bg-gray-50">
		<td colspan="7" class="p-4 border-t border-gray-300">
			<div class="grid grid-cols-2 gap-x-6 gap-y-4">
				<div class="flex items-center col-span-2">
					<span class="font-bold text-gray-700 w-32">제목</span>
					<span class="text-gray-900">{{ row.title }}</span>
				</div>
				<div class="flex items-center col-span-2">
					<span class="font-bold text-gray-700 w-32">주소</span>
					<span class="text-gray-900">[{{ row.zipcode }}] {{ row.address }}, {{ row.addrDetail }}</span>
				</div>
				<div class="flex items-center">
					<span class="font-bold text-gray-700 w-32">행사 기간</span>
					<span class="text-gray-900">{{ row.startDate }} ~ {{ row.endDate }}</span>
				</div>
				<div class="flex items-center">
					<span class="font-bold text-gray-700 w-32">유형</span>
					<span class="text-gray-900">{{ row.type }}</span>
				</div>
				<div class="flex items-center">
					<span class="font-bold text-gray-700 w-32">등록일</span>
					<span class="text-gray-900">{{ row.registeredAt }}</span>
				</div>
				<div class="flex items-center">
					<span class="font-bold text-gray-700 w-32">상태</span>
					<span
						:class="[row.status === 'ACTIVE' ? 'bg-[#3FB8AF]' : 'bg-red-500', 'text-white text-xs px-2 py-1 rounded']">
						{{ row.status === 'ACTIVE' ? '활성화' : '비활성화' }}
					</span>
				</div>
				<div class="col-span-2 flex items-start">
					<span class="font-bold text-gray-700 w-32">인프라</span>
					<div class="space-x-2">
						<span v-for="(item, index) in row.infra.split(',')" :key="index"
							class="items-center rounded-xl bg-white shadow border-2 border-[#3FB8AF] px-3 py-1 text-xs text-[#3FB8AF] font-bold">{{ item }}</span>

					</div>
				</div>
			</div>
		</td>
	</tr>
</template>

<style scoped></style>
