<script setup>

defineProps({
	message: String,
});

</script>

<template>
	<div class="flex justify-center items-center my-6">
		<div class="flex items-center gap-2 text-gray-700 font-bold">
			{{ message }}
		</div>
	</div>
</template>

<style scoped></style>
