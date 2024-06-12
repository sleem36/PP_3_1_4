// Загрузка пользователей и заполнение таблицы
function clearUserTable() {
    document.querySelector('#usersTable').innerHTML = ''; // Очищаем содержимое таблицы
}

function getUserList() {
    clearUserTable(); // Вызываем функцию очистки перед загрузкой новых данных

    fetch('/users')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(users => {
            const tableHeader = `
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>LastName</th>
                    <th>Email</th>
                    <th>Age</th>
                    <th>Role</th>
                    <th>Operations</th>
                </tr>
            `;
            const rows = users.map(user => {
                let roles = (user.roles && Array.isArray(user.roles)) ? user.roles.map(role => role.name.replace('ROLE_', '')).join(', ') : '';

                return `<tr>
                           <td>${user.id}</td>
                           <td>${user.name}</td>
                           <td>${user.lastName}</td>
                           <td>${user.email}</td>
                           <td>${user.age}</td>
                           <td>${roles}</td>
                           <td style="display: flex">
                               <button class="btn btn-info mx-3 edit_b" data-userId="${user.id}">Edit</button>
                               <button class="btn btn-danger mx-3 del_b" data-userId="${user.id}">Delete</button>
                           </td>
                       </tr>`;
            });

            // Пересоздаем таблицу с новыми данными
            usersTable.innerHTML = tableHeader + rows.join('');
        })
        .then(() => {
            // Обработчик событий на кнопках Delete с использованием делегирования
            $('#usersTable').on('click', '.del_b', function() {
                let userId = $(this).data('userid');
                deleteUserBtn(userId);
            });
        })
        .catch(error => console.error('Error fetching users:', error));
}

function getUserDataById(userId) {
    return fetch(`/users/${userId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json(); // Возвращаем JSON-представление данных пользователя
        })
        .catch(error => console.error('Error fetching user data:', error));
}

// Обработчик событий на кнопках Delete
$('#usersTable').on('click', '.del_b', function() {
    let userId = $(this).data('userid');
    getUserDataById(userId).then(user => { // Получаем данные пользователя
        // Заполняем данные в модальном окне при открытии
        $('#idDeleteModal').val(user.id);
        $('#firstnameDeleteModal').val(user.name);
        $('#lastNameDeleteModal').val(user.lastName);
        $('#ageDeleteModal').val(user.age);
        $('#emailDeleteModal').val(user.email);
        // Заполняем роли пользователя в select, убирая префикс "ROLE_"
        let rolesSelect = $('#rolesDeleteModal');
        rolesSelect.empty();
        user.roles.forEach(role => {
            let roleName = role.name.replace('ROLE_', '');
            rolesSelect.append(`<option value="${roleName}">${roleName}</option>`);
        });
        // Показываем модальное окно
        $('#myModalDelete').modal('show');

        // Обработка клика по кнопке "OK" в модальном окне только один раз при открытии модального окна
        $('#confirmDeleteBtn').off().on('click', async function (event) {
            event.preventDefault(); // Предотвращение стандартного действия кнопки

            try {
                const response = await fetch(`/users/${userId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                if (response.ok) {
                    console.log(`Пользователь с ID ${userId} успешно удален.`);
                    $('#myModalDelete').modal('hide');
                    getUserList(); // Обновление списка пользователей
                } else {
                    console.error(`Произошла ошибка при удалении пользователя с ID ${userId}.`);
                }
            } catch (error) {
                console.error('Ошибка при удалении пользователя:', error);
            }
        });
    }).catch(error => console.error('Ошибка при получении данных пользователя:', error));
});

// Обработчик событий на кнопках Edit
$('#usersTable').on('click', '.edit_b', function() {
    let userId = $(this).data('userid');

    getUserDataById(userId).then(user => { // Получаем данные пользователя
        // Заполняем данные в модальном окне при открытии
        $('#idEditModal').val(user.id);
        $('#firstnameEditModal').val(user.name);
        $('#lastNameEditModal').val(user.lastName);
        $('#ageEditModal').val(user.age);
        $('#emailEditModal').val(user.email);
        $('#rolesEditModal').val(user.roleIds);

        // Показываем модальное окно
        $('#myModal').modal('show');

        // Обработка клика по кнопке "OK" в модальном окне
        $('#confirmEditBtn').off().on('click', async function (event) {
            event.preventDefault(); // Предотвращение стандартного действия кнопки

            const selectedRoles = $('#rolesEditModal').val();
            if (selectedRoles.length === 0) {
                alert('Выберите хотя бы одну роль!');
                return; // Прерываем выполнение функции
            }
            const updatedUser = {
                id: $('#idEditModal').val(),
                name: $('#firstnameEditModal').val(),
                lastName: $('#lastNameEditModal').val(),
                age: $('#ageEditModal').val(),
                email: $('#emailEditModal').val(),
                roleIds: $('#rolesEditModal').val(),
                password: $('#password2').val()
            };
            try {
                const response = await fetch(`/users`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(updatedUser) // Отправка обновленных данных пользователя
                });
                if (response.ok) {
                    console.log(`Пользователь с ID ${userId} успешно изменен.`);
                    $('#myModal').modal('hide');
                    getUserList(); // Обновление списка пользователей
                } else {
                    console.error(`Произошла ошибка при изменении пользователя с ID ${userId}.`);
                }
            } catch (error) {
                console.error('Ошибка при изменении пользователя:', error);
            }
        });
    }).catch(error => console.error('Ошибка при получении данных пользователя:', error));
});
