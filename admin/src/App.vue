<template>
  <router-view />
</template>

<script setup>
</script>

<style>
:root {
  --primary: #4A90A4;
  --primary-light: #6BB0C4;
  --primary-dark: #3A7A8E;
  --primary-bg: rgba(74, 144, 164, 0.08);
  --accent: #E07A5F;
  --accent-light: #E8967E;
  --success: #52A88C;
  --warning: #D4A853;
  --danger: #D4726A;
  --text-primary: #1F2937;
  --text-secondary: #4B5563;
  --text-muted: #9CA3AF;
  --bg-page: #F3F4F6;
  --bg-card: #FFFFFF;
  --border: #E5E7EB;
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1);
  --radius: 8px;
  --radius-lg: 12px;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  font-size: 15px;
  color: var(--text-primary);
  background: var(--bg-page);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #D1D5DB;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #9CA3AF;
}

/* Card */
.el-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  transition: box-shadow 0.2s;
}

.el-card:hover {
  box-shadow: var(--shadow);
}

.el-card__header {
  padding: 20px 24px;
  border-bottom: 1px solid var(--border);
}

.el-card__body {
  padding: 24px;
}

/* Button */
.el-button {
  border-radius: var(--radius);
  font-weight: 500;
  font-size: 14px;
  transition: all 0.2s;
}

.el-button--large {
  font-size: 16px;
  padding: 12px 24px;
}

.el-button--default {
  font-size: 14px;
  padding: 9px 20px;
}

.el-button--primary {
  --el-button-bg-color: var(--primary);
  --el-button-border-color: var(--primary);
  --el-button-hover-bg-color: var(--primary-light);
  --el-button-hover-border-color: var(--primary-light);
  --el-button-active-bg-color: var(--primary-dark);
  --el-button-active-border-color: var(--primary-dark);
}

.el-button--success {
  --el-button-bg-color: var(--success);
  --el-button-border-color: var(--success);
  --el-button-hover-bg-color: #6BBF9E;
  --el-button-hover-border-color: #6BBF9E;
}

.el-button--warning {
  --el-button-bg-color: var(--warning);
  --el-button-border-color: var(--warning);
}

.el-button--danger {
  --el-button-bg-color: var(--danger);
  --el-button-border-color: var(--danger);
}

/* Input */
.el-input__wrapper {
  border-radius: var(--radius);
  transition: all 0.2s;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #9CA3AF inset;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px var(--primary) inset;
}

/* Table */
.el-table {
  border-radius: var(--radius);
  overflow: hidden;
  font-size: 15px;
}

.el-table th.el-table__cell {
  background-color: #F9FAFB !important;
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid var(--border);
}

.el-table td.el-table__cell {
  padding: 16px 0;
  border-bottom: 1px solid #F3F4F6;
  color: var(--text-primary);
  font-size: 15px;
}

.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: #FAFBFC;
}

.el-table .cell {
  line-height: 1.5;
}

.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell {
  background-color: #F8FAFC;
}

/* Dialog */
.el-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.el-dialog__header {
  padding: 24px 28px;
  border-bottom: 1px solid var(--border);
  margin: 0;
}

.el-dialog__title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}

.el-dialog__body {
  padding: 28px;
}

.el-dialog__footer {
  padding: 20px 28px;
  border-top: 1px solid var(--border);
}

/* Form */
.el-form-item__label {
  font-weight: 500;
  font-size: 15px;
  color: var(--text-secondary);
}

.el-input__inner {
  color: var(--text-primary);
}

/* Tag */
.el-tag {
  border-radius: 6px;
  font-weight: 500;
}

/* Switch */
.el-switch {
  --el-switch-on-color: var(--primary);
}

/* Pagination */
.el-pagination {
  --el-pagination-button-bg-color: #fff;
}

.el-pager li {
  border-radius: var(--radius);
}

.el-pager li.is-active {
  background-color: var(--primary);
}

/* Divider */
.el-divider__text {
  font-weight: 600;
  color: var(--text-primary);
}

/* Message */
.el-message {
  border-radius: var(--radius);
}

/* Link */
a {
  color: var(--primary);
  text-decoration: none;
}

a:hover {
  color: var(--primary-light);
}
</style>
