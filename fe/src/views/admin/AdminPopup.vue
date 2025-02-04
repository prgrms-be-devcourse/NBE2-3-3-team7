<script setup>
import { ref, computed, onMounted, watch } from "vue";
import DeleteModal from "@/components/admin/popup/DeleteModal.vue";
import BasePaging from "@/components/common/BasePaging.vue";
import DetailRow from "@/components/admin/popup/DetailRow.vue";
import { adminPopups } from '@/services/admin/admin.api'
import { useRoute } from "vue-router";

const route = useRoute();

const isDeleteModalOpen = ref(false);
const selectedUser = ref(null);
const selectedStatus = ref("");
const selectedLocation = ref("");
const selectedType = ref("");

const rows = ref([]);

const expandedRow = ref(null);

const toggleRow = (id) => {
	expandedRow.value = expandedRow.value === id ? null : id;
};


watch(
	() => route.query,
	() => {
		fetchPopupList();
	},
	{ deep: true }
);

onMounted(async () => {
	await fetchPopupList()
})

const fetchPopupList = async () => {
	try {
		const result = await adminPopups(); // route.query를 그대로 전달
		rows.value = result;
		console.log(result)
	} catch (err) {
		console.error('API 요청 오류:', err);
	}
};


const deleteUser = () => {
	console.log(`삭제 성공`);
	// ajax
	isDeleteModalOpen.value = false;
};

const openDeleteModal = (user) => {
	selectedUser.value = user;
	isDeleteModalOpen.value = true;
}

computed(() => {
	return selectedStatus.value && selectedLocation.value && selectedType.value;
});
</script>

<template>
	<main class="flex-col flex-1 overflow-auto height_scrollbar w-full max-w-6xl p-6">
		<div class="flex justify-between items-center mb-6">
			<h1 class="text-2xl font-bold text-gray-700">팝업 관리</h1>
		</div>

		<div class="flex items-center mb-4 space-x-4">
			<label for="">검색</label>
			<input type="text" placeholder="제목 검색"
				class="flex-1 border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring focus:ring-[#3FB8AF]" />
		</div>

		<div class="flex flex-col justify-center overflow-x-auto bg-white border border-gray-200 rounded-lg shadow">
			<table class="table-auto w-full text-center text-sm text-gray-700">
				<thead class="bg-gray-100 text-gray-500 font-bold">
					<tr>
						<th class="p-2">ID</th>
						<th class="p-2">제목</th>
						<th class="p-2">
							<div class="flex items-center justify-center">
								<select v-model="selectedLocation"
									class="border border-gray-300 rounded-lg p-1 text-sm focus:outline-none focus:ring focus:ring-[#3FB8AF]">
									<option value="">지역</option>
									<option value="서울">서울</option>
									<option value="부산">부산</option>
									<option value="대구">대구</option>
									<option value="인천">인천</option>
									<option value="광주">광주</option>
									<option value="대전">대전</option>
									<option value="울산">울산</option>
									<option value="세종">세종</option>
									<option value="경기">경기</option>
									<option value="충북">충북</option>
									<option value="충남">충남</option>
									<option value="전북">전북</option>
									<option value="전남">전남</option>
									<option value="경북">경북</option>
									<option value="경남">경남</option>
									<option value="강원">강원</option>
									<option value="제주">제주</option>
								</select>
							</div>
						</th>
						<th class="p-2">
							<div class="flex items-center justify-center">
								<select v-model="selectedType"
									class="border border-gray-300 rounded-lg p-1 text-sm focus:outline-none focus:ring focus:ring-[#3FB8AF]">
									<option value="">전체</option>
									<option value="식품">식품</option>
									<option value="화장품">화장품</option>
									<option value="패션">패션</option>
									<option value="애니">애니</option>
									<option value="아이돌">아이돌</option>
									<option value="스포츠">스포츠</option>
									<option value="음악">음악</option>
									<option value="테크">테크</option>
									<option value="인테리어">인테리어</option>
									<option value="문화/예술">문화/예술</option>
									<option value="게임">게임</option>
									<option value="헬스">헬스</option>
									<option value="음료">음료</option>
									<option value="책">책</option>
									<option value="친환경">친환경</option>
								</select>
							</div>
						</th>
						<th class="p-2">
							<div class="flex items-center justify-center">
								<select v-model="selectedStatus"
									class="border border-gray-300 rounded-lg p-1 text-sm focus:outline-none focus:ring focus:ring-[#3FB8AF]">
									<option value="">상태</option>
									<option value="active">활성화</option>
									<option value="inactive">비활성화</option>
								</select>
							</div>
						</th>
						<th class="p-2">등록일</th>
						<th class="p-2">관리</th>
					</tr>
				</thead>
				<tbody>
					<template v-for="(row, index) in rows.content" :key="index">
						<tr class="border-t border-gray-300 hover:bg-gray-100 cursor-pointer"
							@click="toggleRow(row.id)">
							<td class="p-2">{{ row.id }}</td>
							<td class="p-2">{{ row.title }}</td>
							<td class="p-2">{{ row.address.slice(0, 2) }}</td>
							<td class="p-2">{{ row.type }}</td>
							<td class="p-2">
								<span
									:class="[row.status === 'ACTIVE' ? 'bg-[#3FB8AF]' : 'bg-red-500', 'text-white text-xs px-2 py-1 rounded']">
									{{ row.status === 'ACTIVE' ? '활성화' : '비활성화' }}
								</span>
							</td>
							<td class="p-2">{{ row.date }}</td>
							<td class="p-2" @click.stop>
								<button
									class="py-2 px-3 bg-white border border-red-500 text-red-500 rounded-lg hover:bg-red-500 hover:text-white text-xs ml-2 transition-colors"
									@click="openDeleteModal()">
									삭제
								</button>
							</td>
						</tr>
						<DetailRow v-if="expandedRow === row.id" :row="row" />
					</template>
				</tbody>
			</table>

			<section class="w-full px-4" aria-label="pagination">
				<div
					class="flex items-center flex-col space-y-2 justify-center border-t border-gray-200 bg-white px-4 py-3">
					<BasePaging v-if="rows.page?.totalPages > 0" :totalPages="rows.page?.totalPages"
						:currentPage="rows.page?.number" url="/admin/popup" />
				</div>
			</section>
			<DeleteModal v-if="isDeleteModalOpen" :user="selectedUser" @close="isDeleteModalOpen = false"
				@delete="deleteUser" />
		</div>
	</main>
</template>

<style scoped></style>
