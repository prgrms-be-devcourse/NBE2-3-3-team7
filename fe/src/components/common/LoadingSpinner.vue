<script setup>

defineProps({
	text: String
})

</script>

<template>
	<div class="flex justify-center items-center h-screen bg-gray-100">
		<div class="text-center">
			<div class="w-12 h-12 border-4 border-t-[#3FB8AF] border-gray-300 rounded-full animate-spin mx-auto"></div>
			<p class="mt-4 text-lg font-semibold text-gray-700">{{ text }}</p>
		</div>
	</div>
</template>

<style scoped></style>