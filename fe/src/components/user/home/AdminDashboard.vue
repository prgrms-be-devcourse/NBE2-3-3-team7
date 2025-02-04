<script setup>
import { RouterLink } from 'vue-router';
</script>

<template>
	<section class="mt-10 w-full flex justify-center">
		<div class="flex flex-col justify-center max-w-4xl w-full items-center">
			<article class="flex flex-col rounded-2xl p-4 bg-white border border-gray-300 shadow-md w-full">
				<div class="flex justify-between pb-4">
					<h3 class="text-lg font-bold">
						관리자 페이지
					</h3>
				</div>
				<div class="border-y border-gray-300">
					<router-link to="/admin" class="flex justify-center text-gray-700 hover:text-[#3FB8AF] items-center my-6">
						<div class="flex items-center gap-2 = font-bold">
							관리자 페이지로 이동
						</div>
					</router-link>
				</div>
			</article>
		</div>
	</section>
</template>

<style scoped></style>
