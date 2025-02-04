<script setup>
import { ref, watch } from "vue";
import { statusChangePopup } from "@/services/user/user.popup";
import { statusChangeLand } from "@/services/user/user.land";

const props = defineProps({
	status: Boolean, // ✅ `Boolean` 값으로 받음
	id: Number,
	type: String,
});

const emit = defineEmits(["update:status"]);

// ✅ `status` 값을 반응형으로 변경
const toggleStatus = ref(props.status);

// ✅ 부모에서 값이 변경되면 `toggleStatus`도 업데이트
watch(() => props.status, (newValue) => {
	toggleStatus.value = newValue;
});

// ✅ 토글 버튼이 변경되었을 때 실행
const statusChanged = async (event) => {
	try {
		toggleStatus.value = event.target.checked; // ✅ UI 즉시 업데이트
		const newStatus = event.target.checked ? "ACTIVE" : "INACTIVE"; // ✅ Boolean → String 변환
		emit("update:status", newStatus); // ✅ 부모 데이터 업데이트


		if (props.type === "popup") {
			await statusChangePopup(props.id, newStatus);
		} else {
			await statusChangeLand(props.id, newStatus);
		}
	} catch (err) {
		console.error(err);
	}
};
</script>

<template>
	<label class="switch-label">
		<span class="font-bold text-xs">{{ toggleStatus ? "활성화" : "비활성화" }}</span>
		<input role="switch" type="checkbox" class="transition-colors" v-model="toggleStatus" @change="statusChanged" />
	</label>
</template>

<style scoped>
.switch-label {
	display: inline-flex;
	align-items: center;
	gap: 0.5rem;
	cursor: pointer;
}

.switch-label>[type="checkbox"] {
	appearance: none;
	position: relative;
	border: max(2px, 0.1em) solid gray;
	border-radius: 1.25em;
	width: 2.25em;
	height: 1.25em;
}

.switch-label>[type="checkbox"]::before {
	content: "";
	position: absolute;
	left: 0;
	width: 1em;
	height: 1em;
	border-radius: 50%;
	transform: scale(0.8);
	background-color: gray;
	transition: left 250ms linear;
}

.switch-label>[type="checkbox"]:checked {
	background-color: #3FB8AF;
	border-color: #3FB8AF;
}

.switch-label>[type="checkbox"]:checked::before {
	background-color: white;
	left: 1em;
}

.switch-label>[type="checkbox"]:disabled {
	border-color: lightgray;
	opacity: 0.7;
	cursor: not-allowed;
}

.switch-label>[type="checkbox"]:disabled:before {
	background-color: lightgray;
}

.switch-label>[type="checkbox"]:disabled+span {
	opacity: 0.7;
	cursor: not-allowed;
}

.switch-label>[type="checkbox"]:focus-visible {
	outline-offset: max(2px, 0.1em);
	outline: max(2px, 0.1em) solid #3FB8AF;
}
</style>