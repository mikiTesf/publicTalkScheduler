# this Python script generates random Congregation, Speaker and Talk names and puts
# them in CSV files. `text/congs.txt`, `text/names.txt` and `text/words.txt` are
# provided as sample word/name sources for this script. You can find sample results
# in this folder (sampleData)
import random

word_list = []

############## ID and talk_title csv ###############
word_file = open('text/words.txt')
csv_file  = open('random_titles.csv', 'w')

for word in word_file:
    word_list.append(word)

csv_file.write('ID,' + 'Talk Number,' + 'Title' + '\n')
for i in range(1, 40):
    first_word  = random.choice(word_list).rstrip('\n')
    middle_word = random.choice(word_list).rstrip('\n')
    last_word   = random.choice(word_list).rstrip('\n')
    csv_file.write(str(i) + ',' + str(random.randint(1, 100)) + ',' + first_word.title() + " " + middle_word.title() + " " + last_word.title() + '\n')

print('talkTitle_csv successfuly created...')
############## ID and cong_name csv ################
word_file = open('text/congs.txt')
csv_file  = open('random_congs.csv', 'w')

word_list.clear()
for word in word_file:
    word_list.append(word)

csv_file.write('ID,' + 'Congregation Name' + '\n')
for i in range(1, 60):
    cong_name = random.choice(word_list).rstrip('\n')
    csv_file.write(str(i) + ',' + cong_name + '\n')

print('cong_csv successfuly created...')
############## ID and elder_detail csv #############
#id firstName    middleName    lastName    phoneNumber    talk_id    congregation_id
word_file = open('text/names.txt')
csv_file  = open('random_elders.csv', 'w')

word_list.clear()
for word in word_file:
    word_list.append(word)

csv_file.write('ID,' + 'First Name,' + 'Middle Name,' + 'Last Name,' + 'Phone Number,'\
                + 'Talk ID,' + 'Congregation ID,' + 'Enabled' + '\n')
for i in range(1, 31):
    _id             = str(i)
    first_name      = random.choice(word_list).rstrip('\n')
    middle_name     = random.choice(word_list).rstrip('\n')
    last_name       = random.choice(word_list).rstrip('\n')
    phoneNumber     = '09' \
                      + str(random.randint(0, 9)) + str(random.randint(0, 9)) \
                      + str(random.randint(0, 9)) + str(random.randint(0, 9)) \
                      + str(random.randint(0, 9)) + str(random.randint(0, 9)) \
                      + str(random.randint(0, 9)) + str(random.randint(0, 9))
    talk_id         = str(random.randint(1, 20))
    congregation_id = str(random.randint(1, 20))
    csv_file.write(_id + ',' + first_name + ',' + middle_name + ',' + last_name + ',' + phoneNumber + ',' + talk_id + ',' + congregation_id + '\n')

print('elder_csv successfuly created...')
