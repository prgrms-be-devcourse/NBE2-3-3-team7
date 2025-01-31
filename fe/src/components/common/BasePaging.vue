<script setup>
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

const props = defineProps({
	totalPages: Number,
	currentPage: Number
});

// TODO: 추후 데이터 많이 넣어보고 테스트 필요
const displayedPages = computed(() => {
	if (props.totalPages < 1) return [];

	const startPage = Math.max(1, props.currentPage - 2);
	const endPage = Math.min(startPage + 4, props.totalPages);

	return Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);
});

const changePage = (page) => {
	const newPage = Number(page);
	if (newPage >= 0 && newPage < props.totalPages) {
		router.push({ path: '/land', query: { ...route.query, page: newPage } });
	}
};
</script>

<template>
	<nav v-if="totalPages > 0" id="paging" class="isolate inline-flex border border-gray-300 rounded-md shadow-sm"
		aria-label="Pagination">
		<button @click="changePage(currentPage - 1)" :disabled="currentPage <= 0"
			class="inline-flex items-center rounded-l-md px-2 py-2 text-gray-400 hover:bg-gray-100 disabled:opacity-50">
			<div class="size-5">
				<i class="fas fa-angle-left"></i>
			</div>
		</button>

		<template v-for="page in displayedPages" :key="page">
			<span v-if="currentPage === (page - 1)"
				class="inline-flex items-center px-4 py-2 text-sm font-semibold bg-[#3FB8AF] text-white">
				{{ page }}
			</span>
			<button v-else @click="changePage(page - 1)"
				class="inline-flex items-center px-4 py-2 text-sm font-semibold text-gray-900 hover:bg-gray-100">
				{{ page }}
			</button>
		</template>

		<button @click="changePage(currentPage + 1)" :disabled="currentPage >= totalPages - 1"
			class="inline-flex items-center rounded-r-md px-2 py-2 text-gray-400 hover:bg-gray-100 disabled:opacity-50">
			<div class="size-5">
				<i class="fas fa-angle-right"></i>
			</div>
		</button>
	</nav>
</template>