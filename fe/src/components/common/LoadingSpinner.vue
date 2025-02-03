<script setup>

defineProps({
	text: String
})

</script>

<template>
	<main class="flex-grow flex flex-col justify-center items-center h-full">
		<div class="text-center">
			<div class="w-12 h-12 border-4 border-t-[#3FB8AF] border-gray-300 rounded-full animate-spin mx-auto"></div>
			<p class="mt-4 text-lg font-semibold text-gray-700">{{ text ? text : "로딩 중 . . ." }}</p>
		</div>
	</main>
</template>

<style scoped></style>