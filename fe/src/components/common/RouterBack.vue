<script setup>

defineProps({
	link: {
		type: String,
		required: true
	},
	text: {
		type: String,
		required: true
	}
})

</script>

<template>
	<div class="flex py-4 max-w-5xl w-full border-b border-gray-300 justify-center px-4">
		<div class="w-full flex items-center flex-shrink-0 justify-between">
			<router-link :to="link" class="font-bold p-2 space-x-2 flex items-center">
				<i class="fas fa-angles-left"></i>
				<span class="font-bold text-xl">{{ text }}</span>
			</router-link>

			<slot></slot>
		</div>
	</div>
</template>

<style scoped></style>
