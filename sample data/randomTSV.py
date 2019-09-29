#this Python script generates random Congregation, Speaker and Talk names
# and puts  them in a TSV file. `congs.txt`, `names.txt` and `words.txt` are
# provided as sample word/name sources for this script. Check the database in
# `/src/main/resources/database/data.db` to see what column each value after
# a tab corresponds to
import random

word_list = []

############## ID and talk_title tsv ###############
word_file = open('words.txt')
tsv_file  = open('random_titles.tsv', 'w')

for word in word_file:
    word_list.append(word)

for i in range(1, 40):
    first_word  = random.choice(word_list).rstrip('\n')
    middle_word = random.choice(word_list).rstrip('\n')
    last_word   = random.choice(word_list).rstrip('\n')
    tsv_file.write(str(i) + '\t' + str(random.randint(1, 100)) + '\t' + first_word.title() + " " + middle_word.title() + " " + last_word.title() + '\n')

print('talkTitle_tsv successfuly created...')
############## ID and cong_name tsv ################
word_file = open('congs.txt')
tsv_file  = open('random_congs.tsv', 'w')

word_list.clear()
for word in word_file:
    word_list.append(word)

for i in range(1, 60):
    cong_name = random.choice(word_list).rstrip('\n')
    tsv_file.write(str(i) + '\t' + cong_name + '\n')

print('cong_tsv successfuly created...')
############## ID and elder_detail tsv #############
#id firstName    middleName    lastName    phoneNumber    talk_id    congregation_id
word_file = open('names.txt')
tsv_file  = open('random_elders.tsv', 'w')

word_list.clear()
for word in word_file:
    word_list.append(word)

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
    tsv_file.write(_id + '\t' + first_name + '\t' + middle_name + '\t' + last_name + '\t' + phoneNumber + '\t' + talk_id + '\t' + congregation_id + '\n')

print('elder_tsv successfuly created...')
