// ================== 全局 ==================
let memos = [];
let editingId = null;

// ================== 访问后端 ==================
async function loadData() {
    const res = await fetch('http://localhost:8080/memos');
    memos = await res.json();
    renderFan();
}

// ================== 提交表单 ==================
async function submitForm() {
    let data = {
        title: document.getElementById("title").value.trim(),
        desc: document.getElementById("desc").value.trim(),
        tags: document.getElementById("tags").value.split(",").map(x => x.trim()).filter(x => x),
        due: document.getElementById("due").value,
        important: document.getElementById("important").value === "true"
    };

    if (!data.title) return alert("标题不能为空");

    if (editingId) {
        await fetch(`http://localhost:8080/memos/${editingId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
    } else {
        await fetch("http://localhost:8080/memos", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
    }

    // 播放主卡片果冻动画
    playBounce();

    editingId = null;
    resetForm();
    await loadData();

    // 播放新卡片插入动画
    playInsertAnimation();
}

// ================== 果冻弹跳动画 ==================
function playBounce() {
    const card = document.querySelector(".center-card");
    card.classList.add("bounce");

    setTimeout(() => {
        card.classList.remove("bounce");
    }, 600);
}

// ================== 重置表单 ==================
function resetForm() {
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("tags").value = "";
    document.getElementById("due").value = "";
    document.getElementById("important").value = "false";
}

// ================== 删除 ==================
async function deleteMemo(id) {
    const element = document.querySelector(`.memo-card[data-id="${id}"]`);

    // 播放撕裂动画
    if (element) {
        element.classList.add("delete-anim");
        setTimeout(async () => {
            await fetch(`http://localhost:8080/memos/${id}`, { method: "DELETE" });
            loadData();
        }, 500);
    } else {
        await fetch(`http://localhost:8080/memos/${id}`, { method: "DELETE" });
        loadData();
    }
}

// ================== 渲染扇形 ==================
function renderFan() {
    const fan = document.getElementById("fan");
    fan.innerHTML = "";

    let total = memos.length;
    if (total === 0) return;

    let startAngle = -25;
    let endAngle = 25;

    memos.forEach((m, i) => {
        let angle = (startAngle + i * (endAngle - startAngle) / (total - 1));

        const card = document.createElement("div");
        card.className = "memo-card";
        if (m.important) card.classList.add("important");
        card.dataset.id = m.id;

        card.style.setProperty("--angle", angle + "deg");
        card.style.transform = `rotate(${angle}deg)`;

        card.innerHTML = `
            <div class="memo-title">${m.title}</div>
            <div class="memo-tags">${m.tags.join(", ")}</div>
            <div class="memo-date">${m.due || "无到期日"}</div>
            <div class="del-btn" onclick="deleteMemo('${m.id}')">删除</div>
        `;

        fan.appendChild(card);
    });
}

// ================== 新卡片插入动画 ==================
function playInsertAnimation() {
    const fan = document.getElementById("fan");
    const cards = fan.querySelectorAll(".memo-card");
    if (cards.length === 0) return;

    const lastCard = cards[cards.length - 1];

    // 初始状态：在中心卡片位置、透明、缩小
    lastCard.classList.add("insert-start");

    // 强制触发重绘
    lastCard.offsetWidth;

    // 飞入
    lastCard.classList.add("insert-end");

    // 动画结束后清除类名
    setTimeout(() => {
        lastCard.classList.remove("insert-start", "insert-end");
    }, 1000);
}

loadData();
