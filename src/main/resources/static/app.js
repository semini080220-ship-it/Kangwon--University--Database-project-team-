/* ============================================================
   삼척 분산 관광 — front logic (실 API fetch + 시드 폴백)
   ============================================================ */
'use strict';

const CONG = {
  LOW:    { label: '한적', cls: 'calm',  rank: 0, on: 1 },
  MEDIUM: { label: '보통', cls: 'busy',  rank: 1, on: 2 },
  HIGH:   { label: '붐빔', cls: 'crowd', rank: 2, on: 3 },
};
const CAT = { COAST: '해안', MOUNTAIN: '산·계곡', HISTORY: '역사', MINE_CULTURE: '폐광 문화', CAVE: '동굴' };

const ICONS = {
  COAST: '<path d="M2 13c2 0 2-2 4-2s2 2 4 2 2-2 4-2 2 2 4 2 2-2 4-2"/><path d="M2 18c2 0 2-2 4-2s2 2 4 2 2-2 4-2 2 2 4 2 2-2 4-2"/>',
  MOUNTAIN: '<path d="M3 20l6-11 4 7 2-3 6 7z"/>',
  HISTORY: '<path d="M4 9h16M5 9v9M19 9v9M9 9v9M15 9v9M3 20h18M12 3l8 6H4z"/>',
  MINE_CULTURE: '<path d="M12 3l9 9-9 9-9-9z"/>',
  CAVE: '<path d="M4 20v-8a8 8 0 0116 0v8M9 20v-4a3 3 0 016 0v4"/>',
};
const icon = (cat) =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">${ICONS[cat] || ICONS.MOUNTAIN}</svg>`;

const GEM_SVG =
  '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"><path d="M12 3l2.6 5.6L21 9.3l-4.5 4.2 1.1 6.2L12 16.9 6.4 19.7l1.1-6.2L3 9.3l6.4-.7z"/></svg>';

const SEED = [
  { id: 1, name: '쏠비치 삼척', description: '대표 해안 리조트. 성수기마다 발길이 몰린다.', category: 'COAST', congestionLevel: 'HIGH', localGem: false, latitude: 37.46, longitude: 129.17, address: '강원 삼척시 수로부인길' },
  { id: 2, name: '장호항', description: '한국의 나폴리. 투명카약으로 붐비는 포구.', category: 'COAST', congestionLevel: 'HIGH', localGem: false, latitude: 37.30, longitude: 129.31, address: '강원 삼척시 근덕면' },
  { id: 3, name: '초곡용굴촛대바위길', description: '한적한 해안 비경을 잇는 산책로.', category: 'COAST', congestionLevel: 'LOW', localGem: true, latitude: 37.36, longitude: 129.26, address: '강원 삼척시 근덕면' },
  { id: 4, name: '덕봉산 해안생태탐방로', description: '숨은 해안 생태길. 사람보다 새가 많다.', category: 'COAST', congestionLevel: 'LOW', localGem: true, latitude: 37.33, longitude: 129.28, address: '강원 삼척시 근덕면' },
  { id: 5, name: '무건리 이끼폭포', description: '신비로운 산간 이끼폭포. 깊은 숲의 정적.', category: 'MOUNTAIN', congestionLevel: 'LOW', localGem: true, latitude: 37.20, longitude: 129.05, address: '강원 삼척시 도계읍' },
  { id: 6, name: '환선굴', description: '국내 최대 석회동굴. 적당한 발길.', category: 'CAVE', congestionLevel: 'MEDIUM', localGem: false, latitude: 37.27, longitude: 129.05, address: '강원 삼척시 신기면' },
  { id: 7, name: '죽서루', description: '관동팔경의 누각, 보물.', category: 'HISTORY', congestionLevel: 'MEDIUM', localGem: false, latitude: 37.44, longitude: 129.16, address: '강원 삼척시 죽서루길' },
  { id: 8, name: '도계 폐광촌 문화공간', description: '폐광지를 재생한 문화공간. 조용한 영감.', category: 'MINE_CULTURE', congestionLevel: 'LOW', localGem: true, latitude: 37.23, longitude: 129.06, address: '강원 삼척시 도계읍' },
];

const COURSES = [
  { themeLabel: '힐링', name: '한적한 해안 힐링', stops: [
    { n: '초곡용굴촛대바위길', c: '한적 해안 산책', gem: true },
    { n: '덕봉산 해안생태탐방로', c: '생태 탐방', gem: true },
    { n: '죽서루', c: '누각에서 쉼', gem: false } ] },
  { themeLabel: '체험', name: '폐광에서 동굴까지', stops: [
    { n: '도계 폐광촌 문화공간', c: '재생 문화', gem: true },
    { n: '환선굴', c: '석회동굴 탐험', gem: false },
    { n: '무건리 이끼폭포', c: '이끼숲 트레킹', gem: true } ] },
  { themeLabel: '친환경', name: '숲과 물의 분산길', stops: [
    { n: '무건리 이끼폭포', c: '이끼폭포', gem: true },
    { n: '초곡용굴촛대바위길', c: '해안 산책', gem: true },
    { n: '장호항', c: '비수기 방문 권장', gem: false } ] },
];

const state = { attractions: [], filter: 'all', apiLive: false };
const $ = (s) => document.querySelector(s);

/* -------------------- data load -------------------- */
async function loadAttractions() {
  try {
    const res = await fetch('/api/attractions', { headers: { Accept: 'application/json' } });
    if (!res.ok) throw new Error('http ' + res.status);
    const data = await res.json();
    if (!Array.isArray(data) || data.length === 0) throw new Error('empty');
    state.attractions = data;
    state.apiLive = true;
  } catch (e) {
    state.attractions = SEED.map((a) => ({ ...a }));
    state.apiLive = false;
  }
  setApiState();
}

function setApiState() {
  const el = $('#apiState');
  $('#apiText').textContent = state.apiLive ? '실시간 API 연결됨' : '데모 데이터 (API 미연결)';
  el.classList.toggle('live', state.apiLive);
}

/* -------------------- badges / helpers -------------------- */
function badge(level) {
  const m = CONG[level] || CONG.LOW;
  let bars = '';
  for (let i = 1; i <= 3; i++) bars += `<i class="${i <= m.on ? 'on' : ''}"></i>`;
  return `<span class="badge ${m.cls}"><span class="lvl">${bars}</span>${m.label}</span>`;
}
const gemTag = () => `<span class="gem">${GEM_SVG}로컬 명소</span>`;
const fix = (n) => (typeof n === 'number' ? n.toFixed(2) : '—');

/* -------------------- stats -------------------- */
function renderStats() {
  const a = state.attractions;
  const total = a.length || 1;
  const c = { HIGH: 0, MEDIUM: 0, LOW: 0 };
  a.forEach((x) => { c[x.congestionLevel] = (c[x.congestionLevel] || 0) + 1; });
  const gems = a.filter((x) => x.localGem).length;

  const bar = $('#distBar');
  bar.querySelector('.s-crowd').style.width = (c.HIGH / total * 100) + '%';
  bar.querySelector('.s-busy').style.width = (c.MEDIUM / total * 100) + '%';
  bar.querySelector('.s-calm').style.width = (c.LOW / total * 100) + '%';

  $('#distLegend').innerHTML = [
    ['crowd', '붐빔', c.HIGH], ['busy', '보통', c.MEDIUM], ['calm', '한적', c.LOW],
  ].map(([cls, lbl, n]) =>
    `<span class="item"><span class="swatch" style="background:var(--${cls})"></span>${lbl} <b>${n}</b></span>`).join('');

  animateCount($('#gemCount'), gems);
}

function animateCount(el, to) {
  let cur = 0; const step = Math.max(1, Math.ceil(to / 14));
  clearInterval(el._t);
  el._t = setInterval(() => {
    cur += step; if (cur >= to) { cur = to; clearInterval(el._t); }
    el.textContent = cur;
  }, 40);
}

/* -------------------- filters + cards -------------------- */
function renderFilters() {
  const defs = [
    ['all', '전체', null], ['LOW', '한적', 'calm'], ['MEDIUM', '보통', 'busy'],
    ['HIGH', '붐빔', 'crowd'], ['gem', '로컬 명소', null],
  ];
  $('#filters').innerHTML = defs.map(([f, lbl, cls]) =>
    `<button class="chip" data-filter="${f}" aria-pressed="${state.filter === f}">
      ${cls ? `<span class="cdot" style="background:var(--${cls})"></span>` : ''}${lbl}</button>`).join('');
  $('#filters').querySelectorAll('.chip').forEach((b) =>
    b.addEventListener('click', () => { state.filter = b.dataset.filter; renderFilters(); renderCards(); }));
}

function filtered() {
  const f = state.filter;
  if (f === 'all') return state.attractions;
  if (f === 'gem') return state.attractions.filter((a) => a.localGem);
  return state.attractions.filter((a) => a.congestionLevel === f);
}

function renderCards() {
  const list = filtered();
  $('#cards').innerHTML = list.map((a, i) => {
    const m = CONG[a.congestionLevel] || CONG.LOW;
    return `<article class="card" data-id="${a.id}"
        style="--accent:var(--${m.cls}); animation:rise .6s both; animation-delay:${i * 0.04}s">
      ${a.localGem ? gemTag() : ''}
      <div class="cat">${CAT[a.category] || a.category}</div>
      <div class="name">${a.name}</div>
      <div class="desc">${a.description || ''}</div>
      <div class="meta">${badge(a.congestionLevel)}<span class="coord">${fix(a.latitude)}, ${fix(a.longitude)}</span></div>
    </article>`;
  }).join('');
  $('#cards').querySelectorAll('.card').forEach((el) =>
    el.addEventListener('click', () => openDrawer(byId(+el.dataset.id))));
}

const byId = (id) => state.attractions.find((a) => a.id === id);

/* -------------------- courses -------------------- */
function renderCourses() {
  $('#rail').innerHTML = COURSES.map((c) => `
    <article class="route">
      <div class="theme">${c.themeLabel} 코스</div>
      <h3>${c.name}</h3>
      <ol>${c.stops.map((s) =>
        `<li class="${s.gem ? 'gemstop' : ''}">${s.n}<small>${s.c}</small></li>`).join('')}</ol>
    </article>`).join('');
}

/* -------------------- drawer -------------------- */
const drawer = $('#drawer');
const scrim = $('#scrim');
let currentAttractionId = null; // 현재 드로어가 보여주는 관광지 — 비동기 응답이 엉뚱한 곳에 그리는 것 방지

function openDrawer(att) {
  if (!att) return;
  currentAttractionId = att.id;
  drawer.innerHTML = drawerHTML(att);
  drawer.classList.add('open');
  scrim.classList.add('open');
  drawer.setAttribute('aria-hidden', 'false');
  bindDrawer(att);
  loadAlternatives(att);
  loadComments(att);
}
function closeDrawer() {
  drawer.classList.remove('open');
  scrim.classList.remove('open');
  drawer.setAttribute('aria-hidden', 'true');
}
scrim.addEventListener('click', closeDrawer);
document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeDrawer(); });

function drawerHTML(a) {
  return `
    <div class="drawer-head">
      <div>
        <div class="cat">${CAT[a.category] || a.category}${a.localGem ? ' · 로컬 명소' : ''}</div>
        <h3>${a.name}</h3>
      </div>
      <button class="close" id="closeBtn" aria-label="닫기">✕</button>
    </div>
    <div class="drawer-body">
      <p class="desc">${a.description || ''}</p>
      <dl class="dl">
        <dt>혼잡도</dt><dd id="dHeadBadge">${badge(a.congestionLevel)}</dd>
        <dt>주소</dt><dd>${a.address || '—'}</dd>
        <dt>좌표</dt><dd>${fix(a.latitude)}, ${fix(a.longitude)}</dd>
      </dl>

      <div class="cong-ctl">
        <div class="lbl">실시간 혼잡도 갱신 (PATCH)</div>
        <div class="seg" id="congSeg">
          <button class="calm"  data-lv="LOW"    data-on="${a.congestionLevel === 'LOW' ? 1 : 0}">한적</button>
          <button class="busy"  data-lv="MEDIUM" data-on="${a.congestionLevel === 'MEDIUM' ? 1 : 0}">보통</button>
          <button class="crowd" data-lv="HIGH"   data-on="${a.congestionLevel === 'HIGH' ? 1 : 0}">붐빔</button>
        </div>
      </div>

      <div class="flow-title">
        <h4 class="serif">분산 추천</h4>
      </div>
      <p class="flow-note" id="flowNote">한산한 대안을 찾는 중…</p>
      <div class="flowviz" id="flowviz">
        <svg class="flow-svg" id="flowSvg"></svg>
        <div class="flow-nodes" id="flowNodes"></div>
      </div>

      <div class="comments" id="comments" role="region" aria-label="방문자 댓글">
        <div class="flow-title">
          <h4 class="serif">방문자 댓글</h4>
          <span class="ccount" id="cCount"></span>
        </div>
        <form class="cform" id="cForm" autocomplete="off" novalidate aria-label="댓글 작성">
          <label class="sr-only" for="cAuthor">닉네임</label>
          <input class="cname" id="cAuthor" type="text" maxlength="50"
                 placeholder="닉네임" aria-label="닉네임" />
          <label class="sr-only" for="cContent">댓글 내용</label>
          <textarea class="ctext" id="cContent" rows="2" maxlength="1000"
                    placeholder="이곳의 한적함은 어땠나요? 댓글을 남겨보세요." aria-label="댓글 내용"></textarea>
          <button class="csubmit" type="submit" aria-label="댓글 남기기">남기기</button>
        </form>
        <p class="cerr" id="cErr" role="alert" aria-live="assertive"></p>
        <ul class="clist" id="cList" aria-live="polite" aria-busy="false"></ul>
      </div>
    </div>`;
}

function bindDrawer(att) {
  $('#closeBtn').addEventListener('click', closeDrawer);
  $('#congSeg').querySelectorAll('button').forEach((b) =>
    b.addEventListener('click', () => setCongestion(att, b.dataset.lv)));
  const cForm = $('#cForm');
  if (cForm) cForm.addEventListener('submit', (e) => { e.preventDefault(); submitComment(att); });
}

/* -------------------- alternatives (분산 흐름) -------------------- */
async function loadAlternatives(att) {
  let alts = null;
  try {
    const res = await fetch(`/api/attractions/${att.id}/alternatives`, { headers: { Accept: 'application/json' } });
    if (res.ok) alts = await res.json();
  } catch (e) { /* fall through */ }
  if (!Array.isArray(alts)) alts = computeAlternatives(att);
  renderFlow(att, alts);
}

function computeAlternatives(base) {
  const baseRank = (CONG[base.congestionLevel] || CONG.LOW).rank;
  return state.attractions
    .filter((a) => a.category === base.category && a.id !== base.id)
    .filter((a) => (CONG[a.congestionLevel] || CONG.LOW).rank < baseRank || a.localGem)
    .sort((x, y) =>
      (Number(y.localGem) - Number(x.localGem)) ||
      ((CONG[x.congestionLevel] || CONG.LOW).rank - (CONG[y.congestionLevel] || CONG.LOW).rank) ||
      x.name.localeCompare(y.name))
    .slice(0, 3);
}

function nodeRow(a, base) {
  const m = CONG[a.congestionLevel] || CONG.LOW;
  const cls = base ? 'node-base' : 'alt';
  return `<div class="${cls}" ${base ? '' : `data-id="${a.id}"`}>
      <span class="ic">${icon(a.category)}</span>
      <span class="t"><b>${a.name}</b><small>${CAT[a.category] || a.category}${a.localGem ? ' · 로컬 명소' : ''}</small></span>
      <span class="pill ${m.cls}">${m.label}</span>
    </div>`;
}

function renderFlow(base, alts) {
  const note = $('#flowNote');
  const nodes = $('#flowNodes');
  if (!nodes) return;

  if (!alts || alts.length === 0) {
    note.textContent = base.congestionLevel === 'LOW'
      ? '이미 한적한 곳이에요. 마음껏 머무세요.'
      : '같은 결의 한적한 대안이 아직 없어요.';
    nodes.innerHTML = nodeRow(base, true) + `<div class="empty-alt">추천할 대안이 없습니다.</div>`;
    $('#flowSvg').innerHTML = '';
    return;
  }

  note.innerHTML = `<b>${base.name}</b>의 발걸음을 이곳으로 흩어보세요 — 같은 ${CAT[base.category] || ''}, 더 한적한 ${alts.length}곳.`;
  nodes.innerHTML = nodeRow(base, true) +
    `<div class="alts">${alts.map((a, i) =>
      nodeRow(a).replace('class="alt"', `class="alt" style="animation-delay:${0.12 + i * 0.1}s"`)).join('')}</div>`;

  nodes.querySelectorAll('.alt').forEach((el) =>
    el.addEventListener('click', () => { const a = byId(+el.dataset.id); if (a) openDrawer(a); }));

  setTimeout(drawConnectors, 80);
}

function drawConnectors() {
  const viz = $('#flowviz'); const svg = $('#flowSvg');
  if (!viz || !svg) return;
  const base = viz.querySelector('.node-base');
  const alts = [...viz.querySelectorAll('.alt')];
  if (!base || alts.length === 0) { svg.innerHTML = ''; return; }
  const r0 = viz.getBoundingClientRect();
  svg.setAttribute('width', viz.clientWidth);
  svg.setAttribute('height', viz.clientHeight);

  const b = base.getBoundingClientRect();
  const x1 = b.left + b.width / 2 - r0.left;
  const y1 = b.bottom - r0.top;

  svg.innerHTML = alts.map((el) => {
    const a = el.getBoundingClientRect();
    const x2 = a.left + 24 - r0.left;
    const y2 = a.top + a.height / 2 - r0.top;
    const my = y1 + (y2 - y1) * 0.45;
    return `<path d="M${x1},${y1} C${x1},${my} ${x2},${y2 - 26} ${x2},${y2}" />`;
  }).join('');
}
window.addEventListener('resize', () => { if (drawer.classList.contains('open')) drawConnectors(); });

/* -------------------- congestion update -------------------- */
async function setCongestion(att, level) {
  if (att.congestionLevel === level) return;
  att.congestionLevel = level; // optimistic, shared ref in state
  if (state.apiLive) {
    try {
      await fetch(`/api/attractions/${att.id}/congestion`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ congestionLevel: level }),
      });
    } catch (e) { /* keep optimistic */ }
  }
  // refresh UI
  renderStats();
  renderCards();
  $('#dHeadBadge').innerHTML = badge(level);
  $('#congSeg').querySelectorAll('button').forEach((b) =>
    b.setAttribute('data-on', b.dataset.lv === level ? 1 : 0));
  loadAlternatives(att);
}

/* -------------------- comments (방문자 댓글) -------------------- */
function fmtDate(iso) {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return '';
  const p = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}.${p(d.getMonth() + 1)}.${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`;
}

/**
 * 댓글 한 개를 DOM 노드로 생성. 작성자·내용은 textContent 로만 넣어
 * 브라우저가 사용자 입력을 HTML 로 파싱하지 않게 한다 (저장형 XSS 원천 차단).
 */
function commentEl(c) {
  const li = document.createElement('li');
  li.className = 'citem' + (c._local ? ' local' : '');

  const head = document.createElement('div');
  head.className = 'chead';
  const who = document.createElement('b');
  who.textContent = c.author;
  const when = document.createElement('time');
  when.textContent = fmtDate(c.createdAt);
  head.append(who, when);

  const body = document.createElement('p');
  body.textContent = c.content;

  li.append(head, body);
  return li;
}

function renderComments(list, attractionId) {
  // 비동기 응답이 도착했을 때 사용자가 이미 다른 관광지로 넘어갔으면 그리지 않는다 (race 방지)
  if (attractionId != null && attractionId !== currentAttractionId) return;
  const wrap = $('#cList');
  if (!wrap) return; // 드로어가 닫혔거나 다른 장소로 바뀜
  const safe = Array.isArray(list) ? list : [];
  const cnt = $('#cCount');
  if (cnt) {
    cnt.textContent = safe.length ? String(safe.length) : '';
    cnt.setAttribute('aria-label', `댓글 ${safe.length}개`);
  }

  wrap.replaceChildren();
  if (!safe.length) {
    const empty = document.createElement('li');
    empty.className = 'cempty';
    empty.textContent = '아직 댓글이 없어요. 첫 댓글을 남겨보세요.';
    wrap.appendChild(empty);
    return;
  }
  safe.forEach((c) => wrap.appendChild(commentEl(c)));
}

async function loadComments(att) {
  const attId = att.id;
  const wrap = $('#cList');
  if (wrap) wrap.setAttribute('aria-busy', 'true');

  let list = att._comments || null;
  if (state.apiLive) {
    try {
      const res = await fetch(`/api/attractions/${attId}/comments`, { headers: { Accept: 'application/json' } });
      if (res.ok) { list = await res.json(); att._comments = list; }
    } catch (e) { /* keep cached/empty */ }
  }
  renderComments(list || att._comments || [], attId);

  if (attId === currentAttractionId) {
    const w = $('#cList');
    if (w) w.setAttribute('aria-busy', 'false');
  }
}

let commentSubmitting = false; // 전송 중 재진입(엔터 연타 등) 방지

async function submitComment(att) {
  if (commentSubmitting) return;
  const authorEl = $('#cAuthor');
  const contentEl = $('#cContent');
  const errEl = $('#cErr');
  const form = $('#cForm');
  const btn = form ? form.querySelector('.csubmit') : null;
  if (!authorEl || !contentEl) return;

  const author = authorEl.value.trim();
  const content = contentEl.value.trim();
  if (errEl) errEl.textContent = '';
  if (!author) { if (errEl) errEl.textContent = '닉네임을 입력해 주세요.'; authorEl.focus(); return; }
  if (!content) { if (errEl) errEl.textContent = '댓글 내용을 입력해 주세요.'; contentEl.focus(); return; }

  commentSubmitting = true;
  if (btn) btn.disabled = true;
  try {
    if (state.apiLive) {
      const res = await fetch(`/api/attractions/${att.id}/comments`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ author, content }),
      });
      if (!res.ok) {
        let msg = '댓글을 저장하지 못했어요.';
        if (res.status === 404) msg = '관광지를 찾을 수 없어요. 페이지를 새로고침 해주세요.';
        else if (res.status >= 500) msg = '서버 오류예요. 잠시 후 다시 시도해 주세요.';
        else { try { const e = await res.json(); if (e && e.message) msg = e.message; } catch (_) { /* keep default */ } }
        if (errEl) errEl.textContent = msg;
        return;
      }
      const created = await res.json();
      att._comments = [created, ...(att._comments || [])];
    } else {
      // 데모/오프라인 모드: 서버 없이 임시 표시만
      att._comments = [
        { author, content, createdAt: new Date().toISOString(), _local: true },
        ...(att._comments || []),
      ];
      if (errEl) errEl.textContent = '데모 모드 — 임시로만 표시됩니다 (실시간 API 연결 시 저장).';
    }
    contentEl.value = '';
    renderComments(att._comments, att.id);
  } catch (e) {
    if (errEl) errEl.textContent = '네트워크 오류로 저장하지 못했어요.';
  } finally {
    commentSubmitting = false;
    if (btn) btn.disabled = false;
  }
}

/* -------------------- init -------------------- */
(async function init() {
  renderFilters();
  renderCourses();
  await loadAttractions();
  renderStats();
  renderCards();
  const openId = new URLSearchParams(location.search).get('open');
  if (openId) { const a = byId(+openId); if (a) openDrawer(a); }
})();
